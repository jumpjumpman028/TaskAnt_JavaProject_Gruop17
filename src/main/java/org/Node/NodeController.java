package org.Node;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import org.Task.Task;
import org.Task.TaskManager;
import  javafx.scene.control.Label;
import java.awt.*;

public class NodeController {
    private Task task;
    @FXML private StackPane root;
    @FXML private Text label;
    @FXML private ImageView exclamationIcon;
    @FXML private ImageView exclamationIconChange;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    public void setTask(Task task) {
        this.task = task;
        setLabel(task.getName());
        startTimeLabel.setText("開始時日:" + task.getStartDateString() + " " + task.getStartTime());
        endTimeLabel.setText("結束時日:" + task.getEndDateString() + " " + task.getEndTime());

    }
    public void setLabel(String text) {
        label.setText(text);
    }
    @FXML private void initialize() {
        root.setOnMouseEntered(event -> {
            exclamationIcon.setVisible(false);
            exclamationIconChange.setVisible(true);
        });
        root.setOnMouseExited(event -> {
            exclamationIcon.setVisible(true);
            exclamationIconChange.setVisible(false);
        });
    }

}