package org.TeamTask;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.Task.TaskManager;

import java.io.IOException;

public class TeamCell {
    @FXML private Label nameLabel;
    @FXML private TextField inviteCode;
    @FXML private StackPane root;

    private Team team;
    private String hoverColor;
    public void setTeam(Team team) {
        this.team = team;
        nameLabel.setText(team.getTeamName());
        inviteCode.setText(team.getTeamCode());

        // 設定 Tooltip
        Tooltip tip = new Tooltip(
                "團隊名稱：" + team.getTeamName() + "\n" +
                        "團隊人數：" + team.getMemberCount()
        );
        tip.setWrapText(true);
        tip.setShowDelay(Duration.seconds(0.5));
        tip.setHideDelay(Duration.seconds(0));
        Tooltip.install(root, tip);

    }

    public void initialize() {
        // 滑鼠移入時添加 hover 樣式
        root.setOnMouseEntered(e -> {
            // root.setStyle("-fx-background-color: " + hoverColor + "; -fx-border-color: #228be6; -fx-border-radius: 5; -fx-padding: 10;"); 我暫且把它改掉，我覺得他不太好看
            if (!root.getStyleClass().contains("hover-hbox")) {
                root.getStyleClass().add("hover-hbox");
            }
        });

//        // 滑鼠移出時移除 hover 樣式
//        root.setOnMouseExited(e -> {
//            root.getStyleClass().remove("hover-hbox");
//        });
//        root.setOnMouseClicked(event -> {
//            TeamManager.ShowInfo(team, (Stage) root.getScene().getWindow());
//
//        });
    }
}