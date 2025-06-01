package org;



import javafx.application.Platform;

public class TestNotification {
    public static void main(String[] args) {
        // 啟動JavaFX Platform
        Platform.startup(() -> {
            CrossPlatformNotification.show("讚讚");
        });
    }
}
