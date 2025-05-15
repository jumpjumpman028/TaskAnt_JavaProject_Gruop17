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
public class WaterTest implements SceneInterface {
    @FXML private VBox taskListPane;
    @FXML
    private void reloadTasks(){
        refreshTaskList();
        TaskManager.getInstance().FetchDataFromDatabase();
    }
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
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskCell.fxml"));
                HBox cell = loader.load();
                TaskCellController controller = loader.getController();
                controller.setTask(task);
                taskListPane.getChildren().add(cell);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private HBox createTaskCell(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskCell.fxml"));
            HBox cell = loader.load();
            TaskCellController controller = loader.getController();
            controller.setTask(task);
            return cell;
        } catch (Exception e) {
            e.printStackTrace();
            return new HBox(); // 回傳空 HBox 防呆
        }
    }

    @Override
    public void LoadEvent() {
        DeBugConsole.log("LoadEvent " + getClass().getName());
    }

    @Override
    public void UnloadEvent() {
        DeBugConsole.log("UnloadEvent " + getClass().getName());
    }
}