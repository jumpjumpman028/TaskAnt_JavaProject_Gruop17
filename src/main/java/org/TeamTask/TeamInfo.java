package org.TeamTask;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.DeBugConsole;
import org.MainApplication;

///  TeamInfo 可以前往 詳細有關於Team的資訊， 跟TaskInfo差不多


public class TeamInfo {
    public static int TeamID;
    private Team team;
    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView saveImageView, cancelImageView;

    @FXML
    private VBox mainVBox;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Label teamNameLabel, InviteCode, teamLeaderLabel, teamMemberLabel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button saveButton, cancelButton;

    private Image normalSaveImage, pressedSaveImage;
    private Image normalCancelImage, pressedCancelImage;

    private double rootPaneXOffset = 0;
    private double rootPaneYOffset = 0;

    public void initialize() {
        // 初始化按鈕圖片
        setupSaveButton();
        setupCancelButton();

        // 設置拖曳事件
        setupWindowDrag();

        // 初始化文本框
        descriptionTextArea.setWrapText(true);
    }

    public void setTeam(Team team) {
        TeamID = team.getTeamId();
        teamNameLabel.setText(team.getTeamName());
        InviteCode.setText(team.getTeamCode());
        teamLeaderLabel.setText(team.getLeader());
        descriptionTextArea.setText(team.getDescription());
    }

    private void setupSaveButton() {
        normalSaveImage = new Image(getClass().getResource("/images/TextBTN_Medium.png").toExternalForm());
        pressedSaveImage = new Image(getClass().getResource("/images/TextBTN_Medium_Pressed.png").toExternalForm());
        saveImageView.setImage(normalSaveImage);

        saveButton.setOnMousePressed(e -> saveImageView.setImage(pressedSaveImage));
        saveButton.setOnMouseReleased(e -> saveImageView.setImage(normalSaveImage));
        saveButton.setOnAction(event -> goToTeamTaskView());
    }

    private void setupCancelButton() {
        normalCancelImage = new Image(getClass().getResource("/images/CloseButton.png").toExternalForm());
        pressedCancelImage = new Image(getClass().getResource("/images/CloseButton.png").toExternalForm());
        cancelImageView.setImage(normalCancelImage);

        cancelButton.setOnMousePressed(e -> cancelImageView.setImage(pressedCancelImage));
        cancelButton.setOnMouseReleased(e -> cancelImageView.setImage(normalCancelImage));
        cancelButton.setOnAction(event -> closeWindow());
    }

    private void setupWindowDrag() {
        rootPane.setOnMousePressed(event -> {
            rootPaneXOffset = event.getSceneX();
            rootPaneYOffset = event.getSceneY();
        });

        rootPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - rootPaneXOffset);
            stage.setY(event.getScreenY() - rootPaneYOffset);
        });
    }

    private void goToTeamTaskView() {
        try {
            MainApplication.switchScene("TeamTaskView.fxml");
        }catch (Exception e) {
            e.printStackTrace();
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}