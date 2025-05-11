package org;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TaskInfoController {
    @FXML
    private Label nameLabel;
    @FXML private Label timeLabel;
    @FXML private Label statusLabel;
    @FXML private Label descLabel;
    @FXML private Label typeLabel;

    public void setTask(Task task) {
        nameLabel.setText(task.getName());
        timeLabel.setText("開始：" + task.getStartDateString() + " " + task.getStartTimeString()
                + "\n結束：" + task.getEndDateString() + " " + task.getEndTimeString());
        statusLabel.setText(task.getStatus().GetString());
        descLabel.setText(task.getDescription());
        typeLabel.setText(task.getType().GetString());
    }
}