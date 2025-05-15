package org;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TaskInfoController {
    @FXML private Label NameLabel;
    @FXML private Label StarttimeLabel;
    @FXML private Label EndtimeLabel;
    @FXML private Label statusLabel;
    @FXML private Label assigneeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label TypeLabel;
    //@FXML private Label recurringDaysLabel;
    public void setTask(Task task) {
        NameLabel.setText(task.getName());
        StarttimeLabel.setText("開始：" + task.getStartDateString() + " 時間" + task.getStartTimeString());
        EndtimeLabel.setText("結束：" + task.getEndDateString() + " 時間" + task.getEndTimeString());
        statusLabel.setText("任務情形 : " + task.getStatus().GetString());
        assigneeLabel.setText("受理者 : " +task.getAssignee());
        descriptionLabel.setText(task.getDescription());
        TypeLabel.setText("任務種類 : " + task.getType().GetString());

    }
}