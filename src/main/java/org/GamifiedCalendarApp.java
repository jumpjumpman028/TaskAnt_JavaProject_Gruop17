package org;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.YearMonth;

public class GamifiedCalendarApp extends Application {

    private YearMonth currentYearMonth;
    private GridPane calendarGrid;
    private Label monthLabel;
    private ProgressBar expBar;
    private Label levelLabel;

    private int experience = 0;
    private int level = 1;

    @Override
    public void start(Stage primaryStage) {
        currentYearMonth = YearMonth.now();

        // 主容器
        BorderPane root = new BorderPane();
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));

        // 頭像與經驗區
        HBox profileBox = new HBox(20);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(5));

        // 模擬角色頭像（可換成 ImageView）
        Label avatar = new Label("🧙‍♂️");
        avatar.setStyle("-fx-font-size: 30px;");

        // 等級與經驗條
        VBox statsBox = new VBox(5);
        levelLabel = new Label("Lv. " + level);
        expBar = new ProgressBar(0);
        expBar.setPrefWidth(200);
        statsBox.getChildren().addAll(levelLabel, expBar);

        profileBox.getChildren().addAll(avatar, statsBox);

        // 月份切換與標籤
        HBox monthControls = new HBox(10);
        monthControls.setAlignment(Pos.CENTER);
        Button prevMonth = new Button("←");
        Button nextMonth = new Button("→");
        monthLabel = new Label();
        monthLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        monthControls.getChildren().addAll(prevMonth, monthLabel, nextMonth);

        calendarGrid = new GridPane();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setAlignment(Pos.CENTER);

        topBox.getChildren().addAll(profileBox, monthControls);
        root.setTop(topBox);
        root.setCenter(calendarGrid);

        // 月份切換事件
        prevMonth.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });
        nextMonth.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        updateCalendar();

        Scene scene = new Scene(root, 650, 550);
        primaryStage.setTitle("Gamified Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();

        LocalDate firstDay = currentYearMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue(); // 1 (Mon) to 7 (Sun)
        int daysInMonth = currentYearMonth.lengthOfMonth();
        monthLabel.setText(currentYearMonth.getMonth() + " " + currentYearMonth.getYear());

        int col = dayOfWeek % 7;
        int row = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefSize(70, 70);
            dayButton.setStyle("-fx-background-color: lightblue; -fx-font-weight: bold;");

            int selectedDay = day;
            dayButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("任務清單");
                alert.setHeaderText("你選擇了 " + currentYearMonth.getYear() + "/" +
                        currentYearMonth.getMonthValue() + "/" + selectedDay);
                alert.setContentText("這天的任務：\n - 完成報告\n - 健身30分鐘\n\n是否標記為完成？");

                ButtonType done = new ButtonType("完成任務");
                ButtonType cancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(done, cancel);

                alert.showAndWait().ifPresent(response -> {
                    if (response == done) {
                        gainExperience(20); // 模擬完成任務加經驗
                        dayButton.setStyle("-fx-background-color: gold; -fx-font-weight: bold;");
                    }
                });
            });

            calendarGrid.add(dayButton, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void gainExperience(int amount) {
        experience += amount;
        if (experience >= 100) {
            experience -= 100;
            level++;
            showLevelUpDialog();
        }
        expBar.setProgress(experience / 100.0);
        levelLabel.setText("Lv. " + level);
    }

    private void showLevelUpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("升級啦！");
        alert.setHeaderText("恭喜你升到 Lv. " + level + " 🎉");
        alert.setContentText("繼續努力完成任務來變強吧！");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
