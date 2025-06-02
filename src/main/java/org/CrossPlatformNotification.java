package org;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CrossPlatformNotification {

    // 外部呼叫這個方法顯示通知
    public static void show(String message) {
        // 在JavaFX Application Thread中執行UI
        Platform.runLater(() -> {
            try {
                new CrossPlatformNotification().showNotificationStage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showNotificationStage(String message) {
        Stage primaryStage = new Stage();

        // 這邊請確定圖片路徑正確，或改成你自己的圖片路徑
        Image image = new Image(getClass().getResource("/images/notification.jpg").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);

        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(250);
        label.setStyle("""
                -fx-background-color: white;
                -fx-padding: 12px;
                -fx-text-fill: black;
                -fx-font-size: 14px;
                -fx-background-radius: 8px;
                """);

        Button closeBtn = new Button("X");
        closeBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: black;
                -fx-font-weight: bold;
                -fx-font-size: 14px;
                -fx-padding: 0 6 0 6;
                """);
        closeBtn.setOnAction(e -> slideOut(primaryStage));

        HBox content = new HBox(10, imageView, label, closeBtn);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("""
                -fx-background-color: white;
                -fx-padding: 12px;
                -fx-background-radius: 12px;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.5, 0, 2);
                """);

        StackPane root = new StackPane(content);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root, 420, 100);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double targetX = screenBounds.getMaxX() - 440;
        double targetY = screenBounds.getMinY() + 20;
        double startX = screenBounds.getMaxX();
        double startY = targetY;

        primaryStage.setX(startX);
        primaryStage.setY(startY);

        primaryStage.show();

        slideIn(primaryStage, startX, targetX, startY);

        PauseTransition wait = new PauseTransition(Duration.seconds(5));
        wait.setOnFinished(e -> slideOut(primaryStage));
        wait.play();
    }

    private void slideIn(Stage stage, double startX, double targetX, double y) {
        WritableValue<Double> writableX = new WritableValue<>() {
            @Override
            public Double getValue() {
                return stage.getX();
            }

            @Override
            public void setValue(Double value) {
                stage.setX(value);
            }
        };

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(writableX, startX)),
                new KeyFrame(Duration.millis(500), new KeyValue(writableX, targetX)));
        timeline.play();
    }

    private void slideOut(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double endX = screenBounds.getMaxX();

        WritableValue<Double> writableX = new WritableValue<>() {
            @Override
            public Double getValue() {
                return stage.getX();
            }

            @Override
            public void setValue(Double value) {
                stage.setX(value);
            }
        };

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(writableX, stage.getX())),
                new KeyFrame(Duration.millis(500), new KeyValue(writableX, endX)));
        timeline.setOnFinished(e -> stage.close());
        timeline.play();
    }
}



