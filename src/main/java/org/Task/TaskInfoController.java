package org.Task;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.DeBugConsole;
import org.WaterTest;

import java.net.URL;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class TaskInfoController {
    @FXML private TextField NameTextField;
    @FXML private Label StarttimeLabel;
    @FXML private Label EndtimeLabel;
    @FXML private Label statusLabel;
    @FXML private Label assigneeLabel;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label TypeLabel;
    @FXML private ComboBox<Task.Status> statusComboBox;
    @FXML private CheckBox recurringDaysCheckBox11;
    @FXML private CheckBox recurringDaysCheckBox12;
    @FXML private CheckBox recurringDaysCheckBox13;
    @FXML private CheckBox recurringDaysCheckBox14;
    @FXML private CheckBox recurringDaysCheckBox15;
    @FXML private CheckBox recurringDaysCheckBox16;
    @FXML private CheckBox recurringDaysCheckBox17;
    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<Integer> startHourComboBox;
    @FXML private ComboBox<Integer> startMinuteComboBox;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<Integer> endHourComboBox;
    @FXML private ComboBox<Integer> endMinuteComboBox;
    @FXML private Button saveButton;
    @FXML private ImageView saveImageView;
    private Image normalSaveImage;
    private Image pressedSaveImage;
    @FXML private Button cancelButton;
    @FXML private ImageView cancelImageView;
    private Image normalCancelImage;
    private Image pressedCancelImage;
    @FXML private Label ErrorLabel;
    @FXML private AnchorPane rootPane;
    private double rootPaneXOffset = 0;
    private double rootPaneYOffset = 0;


    //@FXML private Label recurringDaysLabel;

        private String hoverColor;
    public void setTask(Task task) {
        saveButton.setOnAction(event -> saveTask(task));
        setTaskinitialize(task);
        NameTextField.setText(task.getName());
        UpdateStarttimeLabel(task);
        UpdateEndtimeLabel(task);
        statusLabel.setText("任務情形 : " );

        statusComboBox.getItems().addAll(Task.Status.values());
        statusComboBox.setCellFactory(lv -> new ListCell<Task.Status>() {
            @Override
            protected void updateItem(Task.Status item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.GetString());
            }
        });
        statusComboBox.setButtonCell(new ListCell<Task.Status>() {
            @Override
            protected void updateItem(Task.Status item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.GetString());
            }
        });
        statusComboBox.getSelectionModel().select(task.getStatus());
        if(task.getType() != Task.Type.Experience){
            recurringDaysCheckBox11.setSelected(task.getRecurringDays().contains(DayOfWeek.MONDAY));
            recurringDaysCheckBox12.setSelected(task.getRecurringDays().contains(DayOfWeek.TUESDAY));
            recurringDaysCheckBox13.setSelected(task.getRecurringDays().contains(DayOfWeek.WEDNESDAY));
            recurringDaysCheckBox14.setSelected(task.getRecurringDays().contains(DayOfWeek.THURSDAY));
            recurringDaysCheckBox15.setSelected(task.getRecurringDays().contains(DayOfWeek.FRIDAY));
            recurringDaysCheckBox16.setSelected(task.getRecurringDays().contains(DayOfWeek.SATURDAY));
            recurringDaysCheckBox17.setSelected(task.getRecurringDays().contains(DayOfWeek.SUNDAY));
        }


        if(task.getStartDate() == null) startDatePicker.setValue(task.getStartDate());
        else startDatePicker.setValue(LocalDate.now());
        if(task.getEndDate() == null) endDatePicker.setValue(task.getEndDate());
        else endDatePicker.setValue(LocalDate.now());

        if(task.getStartTime() == null) {
            startHourComboBox.getSelectionModel().selectFirst();
            startMinuteComboBox.getSelectionModel().selectFirst();
        }
        else {
            startHourComboBox.getSelectionModel().select(task.getStartTime().getHour());
            startMinuteComboBox.getSelectionModel().select(task.getStartTime().getMinute());
        }
        if(task.getEndTime() == null){
            endHourComboBox.getSelectionModel().selectFirst();
            endMinuteComboBox.getSelectionModel().selectFirst();
        }else{
            endHourComboBox.getSelectionModel().select(task.getEndTime().getHour());
            endMinuteComboBox.getSelectionModel().select(task.getEndTime().getMinute());
        }



        assigneeLabel.setText("受理者 : " +task.getAssignee());
        descriptionTextArea.setText(task.getDescription());
        TypeLabel.setText("任務種類 : " + task.getType().GetString());

    }
    public void initialize() {

        Platform.runLater(() -> {
            assigneeLabel.requestFocus();
            Stage stage = (Stage) assigneeLabel.getScene().getWindow();
            stage.setMaximized(false);
            stage.setResizable(false);
        });
        rootPane.setOnMousePressed(event -> {
            rootPaneXOffset = event.getSceneX();
            rootPaneYOffset = event.getSceneY();
        });

        rootPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - rootPaneXOffset);
            stage.setY(event.getScreenY() - rootPaneYOffset);
        });
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) hours.add(i);
        ObservableList<Integer> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) minutes.add(i);
        startHourComboBox.setItems(hours);
        endHourComboBox.setItems(hours);
        startMinuteComboBox.setItems(minutes);
        endMinuteComboBox.setItems(minutes);
        SetUpCancelButton();
        SetUpSaveButton();

    }

    private void saveTask(Task task) {
        boolean Error = false;
        // 1. 取得欄位內容
        String newName = NameTextField.getText();
        String newDescription = descriptionTextArea.getText();
        Task.Status newStatus = statusComboBox.getValue();
        // 日期和時間
        LocalDate newStartDate = startDatePicker.getValue();
        LocalDate newEndDate = endDatePicker.getValue();
        Integer newStartHour = startHourComboBox.getValue();
        Integer newStartMinute = startMinuteComboBox.getValue();
        Integer newEndHour = endHourComboBox.getValue();
        Integer newEndMinute = endMinuteComboBox.getValue();

        // 週期性
        ArrayList<DayOfWeek> newRecurringDays = new ArrayList<>();
        if (recurringDaysCheckBox11.isSelected()) newRecurringDays.add(DayOfWeek.MONDAY);
        if (recurringDaysCheckBox12.isSelected()) newRecurringDays.add(DayOfWeek.TUESDAY);
        if (recurringDaysCheckBox13.isSelected()) newRecurringDays.add(DayOfWeek.WEDNESDAY);
        if (recurringDaysCheckBox14.isSelected()) newRecurringDays.add(DayOfWeek.THURSDAY);
        if (recurringDaysCheckBox15.isSelected()) newRecurringDays.add(DayOfWeek.FRIDAY);
        if (recurringDaysCheckBox16.isSelected()) newRecurringDays.add(DayOfWeek.SATURDAY);
        if (recurringDaysCheckBox17.isSelected()) newRecurringDays.add(DayOfWeek.SUNDAY);

        // 2. 更新 Task 物件
        task.setName(newName);
        task.setDescription(newDescription);
        task.setStatus(newStatus);
        try {
            if (newStartDate != null && newEndDate != null && newStartDate.isBefore(newEndDate)) {
                task.setStartDate(newStartDate);
                task.setEndDate(newEndDate);
            }
            Error = false;
        }catch (DateTimeException DT){
            ErrorLabel.setText("開始與結束日期錯誤或不合規定");
            Error = true;
        }
        try {
            if (newStartHour != null && newStartMinute != null)
                task.setStartTime(java.time.LocalTime.of(newStartHour, newStartMinute));
            if (newEndHour != null && newEndMinute != null)
                task.setEndTime(java.time.LocalTime.of(newEndHour, newEndMinute));
            Error = false;
        }catch (DateTimeException DT){
            ErrorLabel.setText(("開始與結束時間錯誤或不合規定"));
            Error = true;
        }
        task.setRecurringDays(newRecurringDays);

        // 3. 你可以在這裡做後續儲存，例如呼叫資料庫、顯示提示等等
        TaskManager.getInstance().UploadDataToDatabase();
        DeBugConsole.log("任務資訊已被更改");
        WaterTest.getInstance().refreshTaskList();
        if(!Error){
            ((Stage)saveButton.getScene().getWindow()).close();
        }

    }

    public void setTaskinitialize(Task task) {
        CheckStatusdinamic(task);
    }
    public void CheckStatusdinamic(Task task) {
        if ("完成".equals(task.getStatus().GetString())) {
            hoverColor = "#00FF7F"; // 已完成任務顯示綠色
        } else if ("進行中".equals(task.getStatus().GetString())) {
            hoverColor = "#ffe066"; // 進行中任務顯示黃色
        } else if("尚未開始".equals(task.getStatus().GetString()) ) {
            hoverColor = "#FF0000"; // 尚未開始任務顯示紅色
        }else{
            hoverColor = "#808080"; // 封鎖任務顯示灰色
        }

    }
    public void UpdateStarttimeLabel(Task task){
        URL url = getClass().getResource("/images/InfoBackGround.png");
        StarttimeLabel.setText("開始：" + task.getStartDateString() + " 時間" + task.getStartTimeString());
    }
    public void UpdateEndtimeLabel(Task task){
        EndtimeLabel.setText("結束：" + task.getEndDateString() + " 時間" + task.getEndTimeString());
    }
    private void SetUpSaveButton(){
        normalSaveImage = new Image(getClass().getResource("/images/TextBTN_Medium.png").toExternalForm());
        pressedSaveImage = new Image(getClass().getResource("/images/TextBTN_Medium_Pressed.png").toExternalForm());
        saveImageView.setImage(normalSaveImage);
        saveButton.setOnAction(event -> onCancelClicked());
        // 按下時換圖
        saveButton.setOnMousePressed(e -> {saveImageView.setImage(pressedSaveImage);
            saveButton.setText("");
        });
        saveButton.setOnMouseReleased(e -> {saveImageView.setImage(normalSaveImage);
            saveButton.setText("儲存");
        });
    }
    private void SetUpCancelButton() {
        normalCancelImage = new Image(getClass().getResource("/images/TextBTN_Cancel.png").toExternalForm());
        pressedCancelImage = new Image(getClass().getResource("/images/TextBTN_Cancel_Pressed.png").toExternalForm());
        cancelImageView.setImage(normalCancelImage);
        cancelButton.setOnAction(event -> onCancelClicked());
        // 按下時換圖
        cancelButton.setOnMousePressed(e -> {
            cancelImageView.setImage(pressedCancelImage);

            });
        cancelButton.setOnMouseReleased(e -> {
            cancelImageView.setImage(normalCancelImage);
        });
    }
    @FXML
    private void onCancelClicked() {
        ((Stage)cancelButton.getScene().getWindow()).close();
    }

}