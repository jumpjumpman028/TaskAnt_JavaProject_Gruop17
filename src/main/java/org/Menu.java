package org;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class Menu {
    @FXML
    private void notification(){
        if (!SystemTray.isSupported()) {
            System.err.println("系統不支援通知功能！");
            return;
        }

        try {
            // 創建系統托盤圖標
            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/notification.jpg")); // 確保圖片路徑正確
            TrayIcon trayIcon = new TrayIcon(image, "任務通知");
            trayIcon.setImageAutoSize(true);

            // 添加到系統托盤
            systemTray.add(trayIcon);

            // 顯示通知
            trayIcon.displayMessage("TaskAnt Notification", "Your team updated your task,go take a look!", TrayIcon.MessageType.INFO);

            // 移除托盤圖標（可選，延遲幾秒後移除）
            Thread.sleep(5000);
            systemTray.remove(trayIcon);
        } catch (Exception e) {
            System.err.println("發送通知時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addTask() {
        // 建立自訂 Dialog
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("新增任務");
        dialog.setHeaderText("請輸入任務資料");

        // 設定按鈕
        ButtonType okButtonType = new ButtonType("確定", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // 建立內容表單
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField taskName = new TextField();
        taskName.setPromptText("任務名稱");
        TextField description = new TextField();
        description.setPromptText("描述");

        DatePicker startDate = new DatePicker(LocalDate.now());
        DatePicker endDate = new DatePicker(LocalDate.now());

        // 小時、分鐘下拉選單
        ComboBox<Integer> startHour = new ComboBox<>();
        ComboBox<Integer> startMinute = new ComboBox<>();
        ComboBox<Integer> endHour = new ComboBox<>();
        ComboBox<Integer> endMinute = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            startHour.getItems().add(i);
            endHour.getItems().add(i);
        }
        for (int i = 0; i < 60; i++) {
            startMinute.getItems().add(i);
            endMinute.getItems().add(i);
        }
        startHour.setValue(9);
        startMinute.setValue(0);
        endHour.setValue(18);
        endMinute.setValue(0);

        // 放進 GridPane
        grid.add(new Label("任務名稱:"), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(new Label("描述:"), 0, 1);
        grid.add(description, 1, 1);
        grid.add(new Label("開始日期:"), 0, 2);
        grid.add(startDate, 1, 2);

        grid.add(new Label("開始時間:"), 0, 3);
        grid.add(startHour, 1, 3);
        grid.add(new Label(":"), 2, 3);
        grid.add(startMinute, 3, 3);

        grid.add(new Label("結束日期:"), 0, 4);
        grid.add(endDate, 1, 4);

        grid.add(new Label("結束時間:"), 0, 5);
        grid.add(endHour, 1, 5);
        grid.add(new Label(":"), 2, 5);
        grid.add(endMinute, 3, 5);

        dialog.getDialogPane().setContent(grid);

        // 取得輸入資料並回傳 TaskData 物件
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String startTime = String.format("%02d:%02d", startHour.getValue(), startMinute.getValue());
                String endTime = String.format("%02d:%02d", endHour.getValue(), endMinute.getValue());
                //return TaskManager.getInstance().CreateTask(taskName.getText(),description.getText(),startDate.getValue(),startHour.getValue(),startMinute.getValue(),endDate.getValue(),endHour.getValue(),endMinute.getValue());
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(data -> {
            System.out.println("任務名稱: " + data.getName());
            System.out.println("描述: " + data.getDescription());
            System.out.println("開始: " + data.getStartDate() + " " + data.getStartTime());
            System.out.println("結束: " + data.getEndDate() + " " + data.getEndTime());
        });
    }
}


