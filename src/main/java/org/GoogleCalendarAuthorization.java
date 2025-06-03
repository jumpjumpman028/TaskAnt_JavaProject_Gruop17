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


    public static String addTaskToGoogleCalendarOnDate(Task task, LocalDate date) {
        try {
            Calendar service = getCalendarService();

            Event event = new Event()
                    .setSummary(task.getName())
                    .setDescription(task.getDescription());

            String startDateTime = date + "T" + task.getStartTimeString() + ":00+08:00";
            event.setStart(new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDateTime)));

            if (task.getEndTime() != null) {
                String endDateTime = date + "T" + task.getEndTimeString() + ":00+08:00";
                event.setEnd(new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(endDateTime)));
            }

            Event created = service.events()
                    .insert("primary", event)
                    .execute();
            System.out.printf("Event created: %s\n", created.getHtmlLink());
            return created.getId();
        } catch (IOException | GeneralSecurityException e) {
            DeBugConsole.log("新增 Google Calendar 事件失敗：" + e.getMessage());
            e.printStackTrace();
            return null;
        }catch (Exception e) {
            DeBugConsole.log("取得服務失敗 原因" + e.getMessage());
            return null;
        }
    }

    public static void addTaskToGoogleCalendarForNextRecurringDays(Task task) {
        LocalDate today = task.getStartDate().isAfter(LocalDate.now())
                ? task.getStartDate()
                : LocalDate.now();
        List<DayOfWeek> ds = task.getRecurringDays();
        if (ds == null || ds.isEmpty()) {
            String id = addTaskToGoogleCalendarOnDate(task, task.getStartDate());
            if (id != null) task.addGoogleEventId(id);
            return;
        }

        List<LocalDate> dates = getNextTargetDates(today, ds, task.getEndDate());
        for (LocalDate d : dates) {
            try {
                String id = addTaskToGoogleCalendarOnDate(task, d);
                if (id != null) {
                    task.addGoogleEventId(id);
                } else {
                    DeBugConsole.log("跳過 " + d + " 的事件（ID 取得失敗）");
                }
            } catch (Exception e) {
                DeBugConsole.log("在 " + d + " 新增事件時遇到未知錯誤：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public static void deleteGoogleEventById(String eventId) {
        try {
            Calendar service = getCalendarService();
            service.events().delete("primary", eventId).execute();
            System.out.println("Event deleted: " + eventId);
        } catch (IOException | GeneralSecurityException e) {
            DeBugConsole.log("刪除單一 Google 事件失敗：" + eventId + "，原因：" + e.getMessage());
            e.printStackTrace();
        }catch (Exception e) {
            DeBugConsole.log("取得服務失敗 原因：" + e.getMessage());
        }
    }

    // 批次刪除 Task 所有事件
    public static void deleteAllGoogleEventsForTask(Task task) {

        Set<String> ids = task.getGoogleEventIds();
        if (ids == null || ids.isEmpty()) return;

        // 使用新集合避免 ConcurrentModificationException
        for (String eventId : new HashSet<>(ids)) {
            try {
                deleteGoogleEventById(eventId);
                task.removeGoogleEventId(eventId);
            } catch (Exception e) {
                // deleteGoogleEventById 已經大部份 catch 掉了，
                // 這裡再做最後一層保險
                DeBugConsole.log("批次刪除事件時錯誤：" + eventId + "，" + e.getMessage());
                e.printStackTrace();
            }
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
    private static List<LocalDate> getNextTargetDates(
            LocalDate today, List<DayOfWeek> targetDays, LocalDate endDay) {
        return targetDays.stream()
                .map(day -> {
                    int delta = (day.getValue() - today.getDayOfWeek().getValue() + 7) % 7;
                    return today.plusDays(delta);
                })
                .filter(date -> !date.isAfter(endDay))  // 含 endDay
                .sorted()
                .collect(Collectors.toList());
    }

}