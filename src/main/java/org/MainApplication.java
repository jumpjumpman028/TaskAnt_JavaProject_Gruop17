package org;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Calendar;

public class MainApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        switchScene("Menu.fxml");
        primaryStage.setTitle("TaskAnt");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        primaryStage.show();
    }


    public static void switchScene(String fxmlFile) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/org/" + fxmlFile));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        try{
            GoogleCalendarAuthorization.getCalendarService();
        }catch (Exception e){
            System.out.println(e);
        }

        launch(args);



    }
}
