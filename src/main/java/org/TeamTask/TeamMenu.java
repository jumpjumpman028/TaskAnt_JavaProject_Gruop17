package org.TeamTask;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.DeBugConsole;
import org.MainApplication;
import org.SceneInterface;
import org.Task.Task;
import org.Task.TaskCellController;
import org.Task.TaskManager;

import java.util.List;
/// 查看所有參與的團隊
public class TeamMenu implements SceneInterface {
    @FXML private VBox teamListPane;

    @Override
    public void LoadEvent(Scene scene) {
        reloadTeams();
        DeBugConsole.log("LoadEvent " + getClass().getName());
    }

    @Override
    public void UnloadEvent() {
        DeBugConsole.log("UnloadEvent " +getClass().getName());
    }

    @Override
    public void FreshEvent() {}

    @FXML
    private void createATeam(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/TeamAddition.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

//            scene.getStylesheets().add(getClass().getResource("/styles/EventForm.css").toExternalForm());


            Stage stage = new Stage();
            stage.setTitle("新增事件");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


            // 新增完任務後，刷新任務清單
            reloadTeams();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void joinATeam(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/JoinTeam.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

//            scene.getStylesheets().add(getClass().getResource("/styles/EventForm.css").toExternalForm());


            Stage stage = new Stage();
            stage.setTitle("新增事件");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


            // 新增完任務後，刷新任務清單
            reloadTeams();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMenu(){
        try {
            MainApplication.switchScene("WaterTest.fxml");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void reloadTeams(){
        TeamManager.getInstance().fetchTeamsFromDatabase();

        teamListPane.getChildren().clear();
        List<Team> teams = TeamManager.getInstance().getTeamList();
        for (Team team : teams) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/TeamCell.fxml"));
                StackPane cell = loader.load();
                TeamCell controller = loader.getController();
                controller.setTeam(team);
                teamListPane.getChildren().add(cell);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private HBox createTeamCell(Team team) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/TeamCell.fxml"));
            HBox cell = loader.load();
            TeamCell controller = loader.getController();
            controller.setTeam(team);
            return cell;
        } catch (Exception e) {
            e.printStackTrace();
            return new HBox(); // 回傳空 HBox 防呆
        }
    }

//    private void openTeamTaskView(int teamId) {
//        SceneSwitcher.getInstance().switchTo("TeamTaskView.fxml", controller -> {
//            TeamTaskView viewController = (TeamTaskView) controller;
//            viewController.setTeamId(teamId); // 傳遞 Team ID
//        });
//    }
}
