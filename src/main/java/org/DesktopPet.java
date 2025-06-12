package org;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DesktopPet {

    private double xOffset = 0;
    private double yOffset = 0;
    private Stage menuStage;

    public void start(Stage primaryStage) {
        Image petImage = new Image(getClass().getResource("/images/knight-geocities.gif").toExternalForm());
        ImageView petView = new ImageView(petImage);
        petView.setFitWidth(80 * 2); // 設定 GIF 的寬度
        petView.setFitHeight(75 * 2); // 設定 GIF 的高度

        VBox speechBubble = createSpeechBubble();

        StackPane root = new StackPane(petView, speechBubble);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root, petView.getFitWidth(), petView.getFitHeight(), Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getWidth() - petView.getFitWidth() - 20);
        primaryStage.setY(screenBounds.getHeight() - petView.getFitHeight() - 20);

        scene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        petView.setPickOnBounds(true);
        petView.setOnMouseClicked(event -> {
            showPetMenu(primaryStage);
        });

        primaryStage.show();
    }

    private VBox createSpeechBubble() {
        Label speechLabel = new Label("你好呀！");
        speechLabel.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-padding: 8 12; -fx-background-radius: 12;");

        Polygon triangle = new Polygon(0.0, 0.0, 10.0, 0.0, 5.0, 7.0);
        triangle.setFill(Color.WHITE);

        VBox speechBubble = new VBox(speechLabel, triangle);
        speechBubble.setAlignment(Pos.CENTER);
        speechBubble.setTranslateY(-80);
        speechBubble.setVisible(false);
        speechBubble.setOpacity(0);

        return speechBubble;
    }

    private void showPetMenu(Stage ownerStage) {
        if (menuStage != null && menuStage.isShowing()) {
            menuStage.toFront();
            return;
        }

        menuStage = new Stage();
        menuStage.initStyle(StageStyle.UTILITY);
        menuStage.setAlwaysOnTop(true);
        menuStage.setTitle("寵物功能選單");

        Label label = new Label("請選擇您需要的功能：");
        label.setStyle("-fx-font-size: 14px;");

        Button waterTestButton = new Button("開啟登入畫面");
        waterTestButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Login.fxml"));
                Scene scene = new Scene(loader.load());

                Stage waterStage = new Stage();
                waterStage.setTitle("登入畫面");
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

        menuStage.setX(ownerStage.getX() - 100);
        menuStage.setY(ownerStage.getY() - 160);

        menuStage.setOnCloseRequest(e -> menuStage = null);
        menuStage.setOnHidden(e -> menuStage = null);

        menuStage.show();
    }
}
