package org;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.util.DateTime;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarQuickstart {
    private static final String APPLICATION_NAME = "TaskAnt Calendar Integration";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();  // 使用 JacksonFactory
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar.readonly");
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials() throws Exception {
        InputStream in = GoogleCalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new Exception("找不到 credentials.json");
        }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in)),
                SCOPES)
                .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String... args) throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        // 取得事件
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("沒有找到事件");
        } else {
            System.out.println("Upcoming events:");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate(); // All-day event
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }
}