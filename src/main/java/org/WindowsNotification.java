package org;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class WindowsNotification {

    public static void main(String[] args) {
        // 檢查系統是否支援 SystemTray
        if (!SystemTray.isSupported()) {
            System.err.println("系統不支援通知功能！");
            return;
        }

        // 創建 SystemTray 和 TrayIcon
        SystemTray systemTray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(WindowsNotification.class.getResource("/images/notification.jpg"));
        TrayIcon trayIcon = new TrayIcon(image, "Java Notification");
        trayIcon.setImageAutoSize(true); // 自動調整圖標大小
        trayIcon.setToolTip("這是 Java 應用程式的通知"); // 提示文字

        try {
            // 將 TrayIcon 添加到 SystemTray
            systemTray.add(trayIcon);

            // 顯示通知
            trayIcon.displayMessage("TaskAnt Notification", "Your teammate just uploaded your task, go take a look!", MessageType.INFO);

            // 延遲幾秒後移除 TrayIcon（可選）
            Thread.sleep(5000); // 5 秒
            systemTray.remove(trayIcon);

        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
