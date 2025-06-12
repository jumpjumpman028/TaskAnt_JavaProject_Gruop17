package org;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.Task.TaskManager;

public class MainApplication extends Application {
    private static SceneInterface SceneController;
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // 初始化主應用程式的主舞台
        primaryStage = stage;
        switchScene("Login.fxml");
        primaryStage.setTitle("TaskManager");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setResizable(false);
        primaryStage.show();

        // 啟動 DesktopPet
        Stage petStage = new Stage();
        DesktopPet desktopPet = new DesktopPet();
        desktopPet.start(petStage);
    }

    public static void switchScene(String fxmlFile) throws Exception {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage is not initialized. Make sure MainApplication is started.");
        }

        if (SceneController != null) SceneController.UnloadEvent();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/" + fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        SceneController = (SceneInterface) fxmlLoader.getController();
        SceneController.LoadEvent(scene);

        String cssFile = "/styles/" + fxmlFile.replace(".fxml", ".css");
        if (MainApplication.class.getResource(cssFile) != null) {
            scene.getStylesheets().add(MainApplication.class.getResource(cssFile).toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            TaskManager.getInstance().stopTaskManager();
            System.out.println("視窗關閉請求！");

        });
    }



public static void main(String[] args) {
        try{
            GoogleCalendarAuthorization.CheckGoogleAuth();
        }catch (Exception e){
            System.out.println(e);
        }

        launch(args);



    }
}
