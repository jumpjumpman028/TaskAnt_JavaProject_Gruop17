package org;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
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
        Image petImage = new Image(getClass().getResource("/images/chiikawa.gif").toExternalForm());
        ImageView petView = new ImageView(petImage);
        petView.setScaleX(0.2);
        petView.setScaleY(0.2);

        
        Label speechLabel = new Label("你好呀！");
        speechLabel.setStyle(
                "-fx-background-color: white; -fx-text-fill: black; -fx-padding: 8 12; -fx-background-radius: 12;");

        
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(
                0.0, 0.0,
                10.0, 0.0,
                5.0, 7.0);
        triangle.setFill(Color.WHITE);

        
        VBox speechBubble = new VBox(speechLabel, triangle);
        speechBubble.setAlignment(Pos.CENTER);
        speechBubble.setTranslateY(-80); 
        speechBubble.setVisible(false);
        speechBubble.setOpacity(0);

        StackPane root = new StackPane(petView, speechBubble);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root, 300, 300, Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.setX(300);
        primaryStage.setY(300);

        scene.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        scene.setOnMouseDragged((MouseEvent event) -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        petView.setPickOnBounds(true);
        petView.setOnMouseClicked(event -> {
            speechLabel.setText("嗨嗨～");
            speechBubble.setVisible(true);
            speechBubble.setOpacity(1.0);

            Timeline showBubble = new Timeline(
                    new KeyFrame(Duration.seconds(0), e -> speechBubble.setOpacity(1.0)),
                    new KeyFrame(Duration.seconds(2), e -> speechBubble.setOpacity(0.0)));
            showBubble.setOnFinished(e -> speechBubble.setVisible(false));
            showBubble.play();
        });

        primaryStage.show();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(32), e -> {
            double currentX = primaryStage.getX();

            if (currentX <= screenBounds.getMinX()) {
                movingRight = true;
            } else if (currentX + primaryStage.getWidth() >= screenBounds.getMaxX()) {
                movingRight = false;
            }

            if (movingRight) {
                petView.setScaleX(-0.2);
                primaryStage.setX(currentX + speed);
            } else {
                petView.setScaleX(0.2);
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

