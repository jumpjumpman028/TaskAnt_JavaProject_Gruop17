package org;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
public class WaterTest {
    @FXML private VBox taskListPane;
    @FXML
    private void handleAddEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EventForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("新增事件");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // 新增完任務後，刷新任務清單
            refreshTaskList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void refreshTaskList() {
        taskListPane.getChildren().clear();
        List<Task> tasks = TaskManager.getInstance().getTaskList();
        for (Task task : tasks) {
            HBox cell = new HBox(10);

            // 任務名稱
            Label nameLabel = new Label(task.getName());

            // 開始時間
            String startTime = "";

                startTime = String.format("%s %s",
                        task.getStartDateString(),
                        task.getStartTimeString());


            // 結束時間
            String endTime = "";

                endTime = String.format("%s %s",
                        task.getEndDateString(),
                        task.getEndTimeString());


            Label timeLabel = new Label("開始：" + startTime + "\n結束：" + endTime);

            // 狀態
            Label statusLabel = new Label(task.getStatus().GetString());

            cell.getChildren().addAll(nameLabel, timeLabel, statusLabel);
            taskListPane.getChildren().add(cell);
        }
    }


    private HBox createTaskCell(Task task) {
        HBox cell = new HBox(10);
        Label name = new Label(task.getName());
        Label status = new Label(task.getStatus().toString());
        cell.getChildren().addAll(name, status);
        // 可以加更多資訊
        return cell;
    }
}