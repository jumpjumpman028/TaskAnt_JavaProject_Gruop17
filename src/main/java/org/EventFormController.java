package org;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

public class EventFormController { // implements Initializable
    @FXML private VBox oneTimePane;
    @FXML private VBox repeatPane;
    @FXML private CheckBox everydayCheck;
    @FXML private CheckBox monCheck;
    @FXML private CheckBox tueCheck;
    @FXML private CheckBox wedCheck;
    @FXML private CheckBox thuCheck;
    @FXML private CheckBox friCheck;
    @FXML private CheckBox satCheck;
    @FXML private CheckBox sunCheck;
    @FXML
    private void ConfirmData(){
        
    }
    @FXML
    private void showOneTimeTask() {
        oneTimePane.setVisible(true);
        repeatPane.setVisible(false);
    }

    @FXML
    private void showRepeatTask() {
        oneTimePane.setVisible(false);
        repeatPane.setVisible(true);
    }
    private boolean internalUpdate = false; // 防止遞迴觸發

    @FXML
    private void onEverydayCheck() {
        if (internalUpdate) return;
        internalUpdate = true;
        boolean selected = everydayCheck.isSelected();
        monCheck.setSelected(selected);
        tueCheck.setSelected(selected);
        wedCheck.setSelected(selected);
        thuCheck.setSelected(selected);
        friCheck.setSelected(selected);
        satCheck.setSelected(selected);
        sunCheck.setSelected(selected);
        internalUpdate = false;
    }

    @FXML
    private void onDayCheck() {
        if (internalUpdate) return;
        internalUpdate = true;
        boolean allSelected = monCheck.isSelected() && tueCheck.isSelected() && wedCheck.isSelected()
                && thuCheck.isSelected() && friCheck.isSelected() && satCheck.isSelected() && sunCheck.isSelected();
        everydayCheck.setSelected(allSelected);
        internalUpdate = false;
    }

}