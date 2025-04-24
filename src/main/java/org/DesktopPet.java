package org;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DesktopPet extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    private double speed = 2;
    private boolean movingRight = true;

    @Override
    public void start(Stage primaryStage) {
        // 角色圖片
        Image petImage = new Image(getClass().getResource("/images/chiikawa.gif").toExternalForm());
        ImageView petView = new ImageView(petImage);

        // 🔽 預設縮小（例如80%大小）
        petView.setScaleX(0.2);
        petView.setScaleY(0.2);

        StackPane root = new StackPane(petView);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root, Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.setX(300);
        primaryStage.setY(300);

        // 拖曳功能
        scene.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        scene.setOnMouseDragged((MouseEvent event) -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        primaryStage.show();

        // 取得螢幕範圍
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(32), e -> {
            double currentX = primaryStage.getX();

            // 邊界碰撞反彈
            if (currentX <= screenBounds.getMinX()) {
                movingRight = true;
            } else if (currentX + primaryStage.getWidth() >= screenBounds.getMaxX()) {
                movingRight = false;
            }

            // ➡️ 根據方向決定水平翻轉
            if (movingRight) {
                petView.setScaleX(-0.2); // ➡️ 水平翻轉 + 縮小
                primaryStage.setX(currentX + speed);
            } else {
                petView.setScaleX(0.2);  // ⬅️ 回到正常 + 縮小
                primaryStage.setX(currentX - speed);
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
