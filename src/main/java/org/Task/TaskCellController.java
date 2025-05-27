package org.Task;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class TaskCellController {
    @FXML private Label nameLabel;
    @FXML private Label starttimeLabel;
    @FXML private Label endtimeLabel;
    @FXML private Label statusLabel;
    @FXML private StackPane root;

    private Task task;
    private String hoverColor;
    public void setTask(Task task) {
        this.task = task;
        nameLabel.setText(task.getName());
        starttimeLabel.setText(
                "開始：" + task.getStartDateString() + " " + task.getStartTimeString()
        );
        endtimeLabel.setText(
                        "結束：" + task.getEndDateString() + " " + task.getEndTimeString()
        );
        statusLabel.setText(task.getStatus().GetString());

        // 設定 Tooltip
        Tooltip tip = new Tooltip(
                "描述：" + task.getDescription() + "\n" +
                        "類型：" + task.getType()
        );
        tip.setWrapText(true);
        tip.setShowDelay(Duration.seconds(0.5));
        tip.setHideDelay(Duration.seconds(0));
        Tooltip.install(root, tip);

        if ("重要".equals(task.getType().GetString())) {
            hoverColor = "#ffa8a8"; // 重要任務顯示紅色
        } else if ("一般".equals(task.getType().GetString())) {
            hoverColor = "#ffe066"; // 一般任務顯示黃色
        } else {
            hoverColor = "#d0ebff"; // 其他預設藍色
        }

    }

    public void initialize() {
        // 滑鼠移入時添加 hover 樣式
        root.setOnMouseEntered(e -> {
            // root.setStyle("-fx-background-color: " + hoverColor + "; -fx-border-color: #228be6; -fx-border-radius: 5; -fx-padding: 10;"); 我暫且把它改掉，我覺得他不太好看
            if (!root.getStyleClass().contains("hover-hbox")) {
                root.getStyleClass().add("hover-hbox");
            }
        });

        // 滑鼠移出時移除 hover 樣式
        root.setOnMouseExited(e -> {
            root.getStyleClass().remove("hover-hbox");
        });
        root.setOnMouseClicked(event -> {
            TaskManager.ShowInfo(task, (Stage) root.getScene().getWindow());

        });
    }
}