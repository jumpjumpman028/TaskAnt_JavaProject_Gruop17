package org.TeamTask;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.MainApplication;
import org.Task.Task;
import org.Task.TaskManager;
import org.TeamTask.TeamTaskManager;
import java.io.IOException;
import java.util.List;


///  從TeamInfo中 點入 關於此Team中的所有Task

public class TeamTaskView {

    @FXML private VBox taskListPane;
    @FXML private Button backButton;
    @FXML private Button addTaskButton;

    private int teamId; // 當前 Team 的 ID

    public void setTeamId(int teamId) {
        this.teamId = teamId;
        loadTeamTasks();
    }

    private void loadTeamTasks() {//載入該TEAM的任務
        // 清空列表
        taskListPane.getChildren().clear();

        // 從 TeamTaskManager 獲取該 Team 的任務
        List<TeamTask> teamTasks = TeamTaskManager.getInstance().getTasksByTeamId(teamId);//todo:還沒測試功能有沒有問題

        // 動態生成任務項目
        for (TeamTask task : teamTasks) {
            Button taskButton = new Button(task.getName());
            taskButton.setOnAction(event -> openTaskDetail(task));
            taskListPane.getChildren().add(taskButton);
        }
    }

    private void openTaskDetail(TeamTask task) {
        TeamTaskManager.getInstance().ShowInfo(task, (Stage) taskListPane.getScene().getWindow());
        // 打開任務詳情或執行其他操作
        System.out.println("打開任務詳情: " + task.getName());
    }

    @FXML
    private void goBack() throws Exception {
        // 返回上一頁
        MainApplication.switchScene("TeamMenu.fxml");
    }

//    @FXML
//    private void addTask() {
//        // 打開新增任務窗口
//        SceneSwitcher.getInstance().openModal("EventForm.fxml", controller -> {
//            EventFormController formController = (EventFormController) controller;
//            formController.setTeamId(teamId); // 傳遞 Team ID
//        });
//    }
}