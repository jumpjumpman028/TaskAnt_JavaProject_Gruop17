package org;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.*;

public class TaskCellController {
    @FXML private Label nameLabel;
    @FXML private Label timeLabel;
    @FXML private Label statusLabel;
    @FXML private HBox root;

    private Task task;
    private String hoverColor;
    public void setTask(Task task) {
        this.task = task;
        nameLabel.setText(task.getName());
        timeLabel.setText(
                "開始：" + task.getStartDateString() + " " + task.getStartTimeString() +
                        "\n結束：" + task.getEndDateString() + " " + task.getEndTimeString()
        );
        statusLabel.setText(task.getStatus().GetString());

        // 設定 Tooltip
        Tooltip tip = new Tooltip(
                "描述：" + task.getDescription() + "\n" +
                        "類型：" + task.getType()
        );
        Tooltip.install(root, tip);
    }
    public void initialize() {
        root.setOnMouseEntered(e -> {
            root.setStyle("-fx-background-color: #d0ebff; -fx-border-color: #228be6; -fx-border-radius: 5; -fx-padding: 10;");
        });
        root.setOnMouseExited(e -> {
            root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");
        });
    }
}