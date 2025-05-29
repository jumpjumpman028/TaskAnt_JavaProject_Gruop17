package org.TeamTask;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Task.Task;
import org.Task.TaskManager;
import org.UserInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeamAddition {

    @FXML private TextField teamName;
    @FXML private TextArea teamDescription;
    @FXML private Label errorLabel;
    @FXML private Button comfirmButton;
    @FXML private Button cancelButton;

    @FXML
    private void ConfirmTeam(){

        String name = teamName.getText();
        String desc = teamDescription.getText();

        if(teamName.getText().isEmpty()){
            errorLabel.setText("團隊名稱不可為空!");
        }else{
            Stage stage = (Stage) comfirmButton.getScene().getWindow();
            List<Integer> teamMembers = new ArrayList<>();
            teamMembers.add(UserInfo.userID);
            TeamManager.getInstance().addTeam(new Team(name,teamMembers,UserInfo.username,desc));
            stage.close();
        }
    }

    @FXML
    private void CancelScene(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
