package org;

import javafx.animation.*;
import javafx.application.Application;
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

public class CrossPlatformNotification extends Application {

    @Override
    public void start(Stage primaryStage) {

        Image image = new Image(getClass().getResource("/images/notification.jpg").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);

        // 設定提示文字
        Label message = new Label("Your teammate just uploaded your task, go take a look!");
        message.setWrapText(true);
        message.setMaxWidth(300);
        message.setStyle("""
                    -fx-background-color: white;
                    -fx-padding: 12px;
                    -fx-text-fill: black;
                    -fx-font-size: 14px;
                    -fx-background-radius: 8px;
                """);

        // 關閉按鈕
        Button closeBtn = new Button("X");
        closeBtn.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: black;
                    -fx-font-weight: bold;
                    -fx-font-size: 14px;
                    -fx-padding: 0 6 0 6;
                """);
        closeBtn.setOnAction(e -> slideOut(primaryStage));

        // 內容容器，加入關閉按鈕
        HBox content = new HBox(10, imageView, message, closeBtn);
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

        // 視窗設定
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double targetX = screenBounds.getMaxX() - 440;
        double targetY = screenBounds.getMinY() + 20;

        // 起始位置（在畫面外，右邊）
        double startX = screenBounds.getMaxX();
        double startY = targetY;

        primaryStage.setX(startX);
        primaryStage.setY(startY);

        primaryStage.show();

        slideIn(primaryStage, startX, targetX, startY);

        // 5秒後自動滑出關閉
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
                new KeyFrame(Duration.millis(0), new KeyValue(writableX, startX)),
                new KeyFrame(Duration.millis(500), new KeyValue(writableX, targetX, Interpolator.EASE_BOTH)));
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
                new KeyFrame(Duration.millis(0), new KeyValue(writableX, stage.getX())),
                new KeyFrame(Duration.millis(500), new KeyValue(writableX, endX, Interpolator.EASE_BOTH)));
        timeline.setOnFinished(e -> stage.close());
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

