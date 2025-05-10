package org;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class EventFormController implements Initializable {
    @FXML private VBox oneTimePane;
    @FXML private VBox repeatPane;
    //一次性任務
    @FXML private TextField oneTaskName;
    @FXML private TextArea oneTaskDesc;
    @FXML private DatePicker oneStartDate;
    @FXML private ComboBox<Integer> oneStartHour;
    @FXML private ComboBox<Integer> oneStartMinute;

    @FXML private TextField repeatTaskName;
    @FXML private TextArea repeatTaskDesc;
    @FXML private DatePicker repeatStartDate;
    @FXML private DatePicker repeatEndDate;
    @FXML private ComboBox<Integer> repeatStartHour;
    @FXML private ComboBox<Integer> repeatStartMinute;
    @FXML private ComboBox<Integer> repeatEndHour;
    @FXML private ComboBox<Integer> repeatEndMinute;
    @FXML private ChoiceBox<String> remindBefore;

    @FXML private Button cancelButton;
    @FXML private Button comfirmButton;
    @FXML private Label errorLabel;

    @FXML private CheckBox everydayCheck;
    @FXML private CheckBox monCheck;
    @FXML private CheckBox tueCheck;
    @FXML private CheckBox wedCheck;
    @FXML private CheckBox thuCheck;
    @FXML private CheckBox friCheck;
    @FXML private CheckBox satCheck;
    @FXML private CheckBox sunCheck;

    private Map<CheckBox, DayOfWeek> dayCheckMap;

    @FXML
    private void ConfirmData(){
        if(oneTimePane.isVisible()){
            String name = oneTaskName.getText();
            String desc = oneTaskDesc.getText();
            LocalDate startDate = oneStartDate.getValue();
            Integer startHour = oneStartHour.getValue();
            Integer startMinute = oneStartMinute.getValue();
            String ErrorMsg = validateInput(name, desc, startDate, startHour, startMinute);
            if(ErrorMsg != null){
                errorLabel.setText(ErrorMsg);
            }else{
                Stage stage = (Stage) comfirmButton.getScene().getWindow();
                TaskManager.getInstance().CreateTask(name,desc,startDate,startHour,startMinute,null,null,null,null,Task.Type.Experience);
                stage.close();
            }


        } else if (repeatPane.isVisible()) {
            String name = repeatTaskName.getText();
            String desc = repeatTaskDesc.getText();
            LocalDate startDate = repeatStartDate.getValue();
            Integer startHour = repeatStartHour.getValue();
            Integer startMinute = repeatStartMinute.getValue();
            LocalDate endDate = repeatEndDate.getValue();
            Integer endHour = repeatEndHour.getValue();
            Integer endMinute = repeatEndMinute.getValue();
            Task.Type taskType = everydayCheck.isSelected() ? Task.Type.BOSS : Task.Type.GENERAL;

            String ErrorMsg = validateInput(name, desc, startDate, startHour, startMinute, endDate, endHour, endMinute);
            if(ErrorMsg != null){
                errorLabel.setText(ErrorMsg);
            }else{
                Stage stage = (Stage) comfirmButton.getScene().getWindow();
                TaskManager.getInstance().CreateTask(name,desc,startDate,startHour,startMinute,endDate,endHour,endMinute,getSelectedDays(),taskType);
                stage.close();
            }
        }
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

    @FXML
    private void CancelScene(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private String validateInput(String name , String desc, LocalDate startDate , Integer startHour, Integer startMinute) {
        if(startDate == null){
            DeBugConsole.log("startDate is null");
        }else if(startDate.isBefore(LocalDate.now())){
            DeBugConsole.log("startDate is before now");
            return "開始日期不得為過去!";
        }
        return null; // 沒有錯誤
    }
    private String validateInput(String name , String desc, LocalDate startDate, Integer startHour, Integer startMinute, LocalDate endDate,Integer endHour, Integer endMinute) {
        if(startDate == null){
            DeBugConsole.log("startDate is null");
        } else if(startDate.isBefore(LocalDate.now())){
            DeBugConsole.log("startDate is before now");
            return "開始日期不得為過去!";
        }
        if(startHour == null || startMinute == null){
            DeBugConsole.log("startHour is null");
            return "開始時間設定錯誤!";
        }
        if(endHour == null || endMinute == null){
            DeBugConsole.log("endHour is null");
            return "結束時間設定錯誤!";
        }
        if( startHour > endHour ){
            DeBugConsole.log("startHour < endHour");
            return "開始時間不得晚於結束時間!!!";
        }else if(startHour.equals(endHour) && startMinute > endMinute){
            DeBugConsole.log("startMinute < endMinute");
            return "開始時間不得晚於結束時間!!!";
        }
        if(endDate != null)
            if(startDate.isAfter(endDate)){
                DeBugConsole.log("startDate is after endDate");
                return "開始日期不得晚於結束日期";
            }
        return null; // 沒有錯誤
    }
    public List<DayOfWeek> getSelectedDays() {
        if (everydayCheck.isSelected()) {
            return Arrays.asList(DayOfWeek.values());
        }
        List<DayOfWeek> selected = new ArrayList<>();
        for (Map.Entry<CheckBox, DayOfWeek> entry : dayCheckMap.entrySet()) {
            if (entry.getKey().isSelected()) {
                selected.add(entry.getValue());
            }
        }
        return selected;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)  {
        // 小時 0~23
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) hours.add(i);
        // 分鐘 0~59
        ObservableList<Integer> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) minutes.add(i);

        // 設定 ComboBox
        oneStartHour.setItems(hours);
        oneStartMinute.setItems(minutes);
        repeatStartHour.setItems(hours);
        repeatStartMinute.setItems(minutes);
        repeatEndHour.setItems(hours);
        repeatEndMinute.setItems(minutes);
        dayCheckMap = new LinkedHashMap<>();
        dayCheckMap.put(monCheck, DayOfWeek.MONDAY);
        dayCheckMap.put(tueCheck, DayOfWeek.TUESDAY);
        dayCheckMap.put(wedCheck, DayOfWeek.WEDNESDAY);
        dayCheckMap.put(thuCheck, DayOfWeek.THURSDAY);
        dayCheckMap.put(friCheck, DayOfWeek.FRIDAY);
        dayCheckMap.put(satCheck, DayOfWeek.SATURDAY);
        dayCheckMap.put(sunCheck, DayOfWeek.SUNDAY);
        // 其他 ComboBox 也一樣設定
        // repeatStartHour.setItems(hours);
        // repeatStartMinute.setItems(minutes);
        // oneTimeHour.setItems(hours);
        // oneTimeMinute.setItems(minutes);
        // repeatEndHour.setItems(hours);
        // repeatEndMinute.setItems(minutes);
    }
}