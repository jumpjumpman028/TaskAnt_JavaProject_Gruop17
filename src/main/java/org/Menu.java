package org;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
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

        TextField startTime = new TextField();
        startTime.setPromptText("開始時間 (HH:mm)");
        TextField endTime = new TextField();
        endTime.setPromptText("結束時間 (HH:mm)");

        grid.add(new Label("任務名稱:"), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(new Label("描述:"), 0, 1);
        grid.add(description, 1, 1);
        grid.add(new Label("開始日期:"), 0, 2);
        grid.add(startDate, 1, 2);
        grid.add(new Label("開始時間:"), 0, 3);
        grid.add(startTime, 1, 3);
        grid.add(new Label("結束日期:"), 0, 4);
        grid.add(endDate, 1, 4);
        grid.add(new Label("結束時間:"), 0, 5);
        grid.add(endTime, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // 取得輸入資料
        //todo:需要改成task物件
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Task(
                        taskName.getText(),
                        description.getText(),
                        startDate.getValue(),
                        startTime.getText(),
                        endDate.getValue(),
                        endTime.getText()
                );
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(data -> {
            System.out.println("任務名稱: " + data.taskName);
            System.out.println("描述: " + data.description);
            System.out.println("開始: " + data.startDate + " " + data.startTime);
            System.out.println("結束: " + data.endDate + " " + data.endTime);
        });
    }

}
