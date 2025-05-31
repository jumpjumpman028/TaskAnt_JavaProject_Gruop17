package org;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.*;
import java.util.Collections;

public class GoogleAuthChecker {

    private static final String TOKENS_DIRECTORY_PATH = "tokens"; // 憑證資料夾

    public static void main(String[] args) throws Exception {
        boolean valid = checkCredential();
        if (!valid) {
            deleteTokenDirectory();
            DeBugConsole.log("憑證已過期或無效，已自動刪除，請重新授權。");
        } else {
            DeBugConsole.log("成功登入");
        }
    }

    public static boolean checkCredential() {

        try {
            FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH));

            // 讀取 credentials.json
            InputStream in = GoogleAuthChecker.class.getResourceAsStream("/credentials.json");
            if (in == null) throw new Exception("找不到 credentials.json");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                    clientSecrets,
                    Collections.singletonList("https://www.googleapis.com/auth/calendar"))
                    .setDataStoreFactory(dataStoreFactory)
                    .setAccessType("offline")
                    .build();

            Credential credential = flow.loadCredential("user");
            System.out.println("credential = " + credential);
            System.out.println("refreshToken = " + (credential != null ? credential.getRefreshToken() : "null"));
            System.out.println("accessToken = " + (credential != null ? credential.getAccessToken() : "null"));
            if (credential == null) {
                return false;
            }
            // 檢查 access token 是否有效
            if (credential.refreshToken()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static void deleteTokenDirectory() {
        File directory = new File("tokens");
        deleteDirectory(directory);
    }

    private static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteDirectory(f); // 遞迴刪除
                }
            }
        }
        file.delete(); // 刪除檔案或資料夾本身
    }
}
