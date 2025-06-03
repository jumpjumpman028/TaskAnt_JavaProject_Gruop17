package org.TeamTask;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.MainApplication;
import org.SceneInterface;

import java.util.List;
///  從TeamInfo中 點入 關於此Team中的所有Task
public class TeamTaskView implements SceneInterface{
    public static TeamTaskView instance = new TeamTaskView();
    public static TeamTaskView getInstance() {
        return instance;
    }
    @FXML private VBox taskListPane;


    private int teamId; // 當前 Team 的 ID

    public void setTeamId(int teamId) {
        this.teamId = teamId;
        reloadTasks();
    }

    @FXML
    public void reloadTasks() {//載入該TEAM的任務
        // 清空列表
        taskListPane.getChildren().clear();
        TeamTaskManager.getInstance().fetchTeamTasks();
        // 從 TeamTaskManager 獲取該 Team 的任務
        List<TeamTask> teamTasks = TeamTaskManager.getInstance().getTasksByTeamId(teamId);//todo:還沒測試功能有沒有問題


        // 動態生成任務項目
        for (TeamTask task : teamTasks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/TeamTaskCell.fxml"));
                StackPane cell = loader.load();
                TeamTaskCell controller = loader.getController();
                controller.setTeamTask(task);
                taskListPane.getChildren().add(cell);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void openTaskDetail(TeamTask task) {
//        TeamTaskManager.getInstance().ShowInfo(task, (Stage) taskListPane.getScene().getWindow());
//        // 打開任務詳情或執行其他操作
//        System.out.println("打開任務詳情: " + task.getName());
//    }

    @FXML
    private void backToMenu() throws Exception {
        // 返回上一頁
        MainApplication.switchScene("TeamMenu.fxml");
    }

    @FXML private void initialize() {
        setTeamId(TeamInfo.TeamID);
        System.out.println("TeamTaskView成功設定TeamID: " + TeamInfo.TeamID);
        reloadTasks();
    }



    @FXML
    private void AddTeamTask() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/TeamTaskForm.fxml"));

            Parent root = loader.load();
            TeamTaskForm controller = loader.getController();
            controller.SetTeamID(teamId);
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/styles/EventForm.css").toExternalForm());


            Stage stage = new Stage();
            stage.setTitle("新增事件");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void LoadEvent() {

    }

    @Override
    public void UnloadEvent() {

    }

    @Override
    public void FreshEvent() {

    }

}