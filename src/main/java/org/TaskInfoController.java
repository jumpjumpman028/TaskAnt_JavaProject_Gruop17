package org;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TaskInfoController {
    @FXML private Label NameLabel;
    @FXML private Label timeLabel;
    @FXML private Label statusLabel;
    @FXML private Label assigneeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label TypeLabel;
    //@FXML private Label recurringDaysLabel;
    public void setTask(Task task) {
        NameLabel.setText(task.getName());
        timeLabel.setText("開始：" + task.getStartDateString() + " " + task.getStartTimeString()
                + "\n結束：" + task.getEndDateString() + " " + task.getEndTimeString());
        statusLabel.setText(task.getStatus().GetString());
        assigneeLabel.setText(task.getAssignee());
        descriptionLabel.setText(task.getDescription());
        TypeLabel.setText(task.getType().GetString());

    }
}