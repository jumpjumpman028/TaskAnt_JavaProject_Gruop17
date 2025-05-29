package org;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.services.calendar.Calendar;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.stream.Collectors;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.Task.Task;


public class GoogleCalendarAuthorization {
    private static final String APPLICATION_NAME = "TaskAnt Calendar Integration";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar");
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static Calendar getCalendarService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {

        InputStream in = GoogleCalendarAuthorization.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new Exception("找不到 credentials.json");
        }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY,
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in)),
                SCOPES)
                .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }


    public static String addTaskToGoogleCalendarOnDate(Task task, LocalDate date) throws Exception {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(task.getName())
                .setDescription(task.getDescription());

        String startDateTime = date.toString() + "T" + task.getStartTimeString() + ":00";
        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTime))
                .setTimeZone("Asia/Taipei");
        event.setStart(start);

        if (task.getEndTime() != null) {
            String endDateTime = date.toString() + "T" + task.getEndTimeString() + ":00";
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endDateTime))
                    .setTimeZone("Asia/Taipei");
            event.setEnd(end);
        }

        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        return event.getId(); // 回傳 eventId
    }

    public static void addTaskToGoogleCalendarForNextRecurringDays(Task task) throws Exception {
        LocalDate today = LocalDate.now();
        List<DayOfWeek> targetDays = task.getRecurringDays();
        if (targetDays == null || targetDays.isEmpty()) {
            String eventId = addTaskToGoogleCalendarOnDate(task, task.getStartDate());
            task.addGoogleEventId(eventId);
            return;
        }
        List<LocalDate> eventDates = getNextTargetDates(today, targetDays);
        for (LocalDate date : eventDates) {
            String eventId = addTaskToGoogleCalendarOnDate(task, date);
            task.addGoogleEventId(eventId);
        }
    }
    public static void deleteGoogleEventById(String eventId) throws Exception {
        Calendar service = getCalendarService();
        service.events().delete("primary", eventId).execute();
        System.out.println("Event deleted: " + eventId);
    }

    // 批次刪除 Task 所有事件
    public static void deleteAllGoogleEventsForTask(Task task) throws Exception {
        for (String eventId : new HashSet<>(task.getGoogleEventIds())) { // 避免ConcurrentModificationException
            deleteGoogleEventById(eventId);
            task.removeGoogleEventId(eventId);
        }
    }
    public static void CheckGoogleAuth(){
        boolean token = GoogleAuthChecker.checkCredential();
        if(!token){
            DeBugConsole.log("憑證已失效請再次獲取");
            GoogleAuthChecker.deleteTokenDirectory();
            File dir = new File("tokens");
            System.out.println("tokens exists? " + dir.exists());
            try{
                getCalendarService();
            }catch(Exception e){
                DeBugConsole.log(e.getMessage());
            }

        }
    }
    public static List<LocalDate> getNextTargetDates(LocalDate today, List<DayOfWeek> targetDays) {
        List<LocalDate> result = new ArrayList<>();
        for (DayOfWeek day : targetDays) {
            int daysUntil = (day.getValue() - today.getDayOfWeek().getValue() + 7) % 7;
            // 如果是今天，則往下一週
            if (daysUntil == 0) daysUntil = 7;
            result.add(today.plusDays(daysUntil));
        }
        return result;
    }

}