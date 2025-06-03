package org;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.shape.Polygon;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DesktopPet {

    private double xOffset = 0;
    private double yOffset = 0;
    private double speed = 2;
    private boolean movingRight = true;
    private Stage menuStage; // 用來追蹤功能選單

    public void start(Stage primaryStage) {
        Image petImage = new Image(getClass().getResource("/images/chiikawa.gif").toExternalForm());
        ImageView petView = new ImageView(petImage);
        petView.setScaleX(0.2);
        petView.setScaleY(0.2);

        VBox speechBubble = createSpeechBubble();

        StackPane root = new StackPane(petView, speechBubble);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root, 300, 300, Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);

        // 設定寵物位置：貼在螢幕底部
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2); // 水平居中
        primaryStage.setY(600); // 貼在螢幕底部

        // 拖動邏輯
        scene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        // 點擊寵物彈出功能選單
        petView.setPickOnBounds(true);
        petView.setOnMouseClicked(event -> {
            showPetMenu();
        });

        primaryStage.show();

        // 寵物自動移動邏輯
        animatePet(primaryStage, petView);
    }

    private VBox createSpeechBubble() {
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

        return speechBubble;
    }

    private void showPetMenu() {
        if (menuStage != null && menuStage.isShowing()) {
            menuStage.toFront(); // 已存在就 bring to front
            return;
        }

        menuStage = new Stage();
        menuStage.initStyle(StageStyle.UTILITY);
        menuStage.setAlwaysOnTop(true);
        menuStage.setTitle("寵物功能選單");

        Label label = new Label("請選擇您需要的功能：");
        label.setStyle("-fx-font-size: 14px;");

        Button waterTestButton = new Button("跳到 WaterTest");

        waterTestButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WaterTest.fxml"));
                Scene scene = new Scene(loader.load());

                Stage waterStage = new Stage();
                waterStage.setTitle("Water Test");
                waterStage.setScene(scene);
                waterStage.initStyle(StageStyle.UTILITY);
                waterStage.setAlwaysOnTop(true);
                waterStage.show();

                menuStage.close();
                menuStage = null;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button cancelButton = new Button("取消");
        cancelButton.setOnAction(e -> {
            menuStage.close();
            menuStage = null;
        });

        VBox layout = new VBox(10, label, waterTestButton, cancelButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px; -fx-background-color: white;");

        Scene scene = new Scene(layout, 250, 150);
        menuStage.setScene(scene);

        // 顯示在寵物正上方
        Stage ownerStage = (Stage) waterTestButton.getScene().getWindow();
        menuStage.setX(ownerStage.getX() + 30);
        menuStage.setY(ownerStage.getY() - 160);

        menuStage.setOnCloseRequest(e -> menuStage = null); // 視窗關閉時釋放資源

        menuStage.show();
    }

    private void animatePet(Stage primaryStage, ImageView petView) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Timeline moveTimeline = new Timeline(new KeyFrame(Duration.millis(32), e -> {
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
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();
    }
}


