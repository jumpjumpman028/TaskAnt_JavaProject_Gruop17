package org.TeamTask;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.CrossPlatformNotification;

public class JoinTeam {
    @FXML private Label errorLabel;
    @FXML private TextField inviteCode;

    @FXML
    private void confirm(){
        String code = inviteCode.getText();

        if(inviteCode.getText().isEmpty()){
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("邀請碼不可為空，請輸入邀請碼");
        }
        else{
            boolean joinable = TeamManager.getInstance().joinTeam(code);
            if(joinable){
                errorLabel.setStyle("-fx-text-fill: #17fd17;");

                errorLabel.setText("成功加入團隊！");

                Stage stage = (Stage) errorLabel.getScene().getWindow();
                stage.close();

            }
            else{
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("無效的邀請碼");
            }
        }
    }

    @FXML
    private void cancel(){
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        stage.close();
    }
}
