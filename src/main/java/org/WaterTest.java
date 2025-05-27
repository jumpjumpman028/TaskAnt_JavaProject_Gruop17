package org;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.Task.Task;
import org.Task.TaskCellController;
import org.Task.TaskManager;

import java.util.List;

public class WaterTest implements SceneInterface {

    @FXML private ScrollPane ScrollPane;
    @FXML private VBox taskListPane;
    public static WaterTest instance;
    public WaterTest() {
        instance = this;
    }
    public static WaterTest getInstance() {
        return instance;
    }
    @FXML
    private void reloadTasks(){
        TaskManager.getInstance().FetchDataFromDatabase();
        refreshTaskList();

    }
    @FXML
    private void handleAddEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EventForm.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/styles/EventForm.css").toExternalForm());


            Stage stage = new Stage();
            stage.setTitle("新增事件");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


            // 新增完任務後，刷新任務清單
            refreshTaskList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshTaskList() {
        taskListPane.getChildren().clear();
        List<Task> tasks = TaskManager.getInstance().getTaskList();
        for (Task task : tasks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskCell.fxml"));
                StackPane cell = loader.load();
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
    @FXML
    private void switchToNodeMap(ActionEvent event) throws Exception {
        MainApplication.switchScene("NodeMapView.fxml");
    }

    @Override
    public void LoadEvent() {
        reloadTasks();
        DeBugConsole.log("LoadEvent " + getClass().getName());

    }

    @Override
    public void UnloadEvent() {
        DeBugConsole.log("UnloadEvent " + getClass().getName());
    }
}