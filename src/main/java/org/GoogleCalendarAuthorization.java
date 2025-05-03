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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;


import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;


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


    public void addTaskToGoogleCalendar(Task task) throws Exception {
        try {
            Calendar service = getCalendarService();

            Event event = new Event()
                    .setSummary(task.getName())
                    .setDescription(task.getDescription());

            // 設定事件時間
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(task.getStartDate().toString() + "T" + task.getStartTime().toString() + ":00"))
                    .setTimeZone("Asia/Taipei");
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(task.getEndDate().toString() + "T" + task.getEndTime().toString() + ":00"))
                    .setTimeZone("Asia/Taipei");
            event.setEnd(end);

            // 新增事件到 Google Calendar
            String calendarId = "primary"; // 主日曆
            event = service.events().insert(calendarId, event).execute();

            System.out.printf("Event created: %s\n", event.getHtmlLink());
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            System.err.println("Security Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}