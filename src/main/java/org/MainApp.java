import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 啟動時你可以先測試一次
        CrossPlatformNotification.show("應用啟動通知");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
