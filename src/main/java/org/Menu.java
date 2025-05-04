package org;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class Menu {

    @FXML
    private void addTask() {
        // 建立自訂 Dialog
        Dialog<TaskData> dialog = new Dialog<>();
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
                return new TaskData(
                        taskName.getText(),
                        description.getText(),
                        startDate.getValue(),
                        startTime,
                        endDate.getValue(),
                        endTime
                );
            }
            return null;
        });

        Optional<TaskData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            System.out.println("任務名稱: " + data.taskName);
            System.out.println("描述: " + data.description);
            System.out.println("開始: " + data.startDate + " " + data.startTime);
            System.out.println("結束: " + data.endDate + " " + data.endTime);

            try {
                saveTaskToDatabase(data);
                System.out.println("任務已成功新增至資料庫！");
            } catch (SQLException e) {
                System.err.println("新增任務時發生錯誤：" + e.getMessage());
            }
        });
    }

    private void saveTaskToDatabase(TaskData taskData) throws SQLException {
        String dbUrl = "jdbc:mysql://yamanote.proxy.rlwy.net:44528/taskant_userinfo";
        String dbUser = "root";
        String dbPassword = "zrKLjtYqVNzwFAVvMtklGAWgKlGHFPhb";

        String insertTaskSQL = "INSERT INTO tasks (task_name, task_description, start_date, start_time, end_date, end_time) VALUES (?, ?, ?, ?, ?, ?)";
        //                            mySQL table名稱(各column名稱)
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(insertTaskSQL)) {

            preparedStatement.setString(1, taskData.taskName);//數字對應幾個?
            preparedStatement.setString(2, taskData.description);
            preparedStatement.setDate(3, java.sql.Date.valueOf(taskData.startDate));
            preparedStatement.setString(4, taskData.startTime);
            preparedStatement.setDate(5, java.sql.Date.valueOf(taskData.endDate));
            preparedStatement.setString(6, taskData.endTime);

            preparedStatement.executeUpdate();
        }
    }

    // 建議加 public 方便外部存取
    public static class TaskData {
        public String taskName;
        public String description;
        public LocalDate startDate;
        public String startTime;
        public LocalDate endDate;
        public String endTime;

        public TaskData(String taskName, String description, LocalDate startDate, String startTime, LocalDate endDate, String endTime) {
            this.taskName = taskName;
            this.description = description;
            this.startDate = startDate;
            this.startTime = startTime;
            this.endDate = endDate;
            this.endTime = endTime;
            //測試用
            TaskManager.getInstance().addTask(new Task(taskName,description,"",startDate,startTime));
        }
    }
}
