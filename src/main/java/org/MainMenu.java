package org;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import org.Task.Task;
import org.Task.TaskCellController;
import org.Task.TaskManager;

import java.util.List;

public class MainMenu implements SceneInterface {
    @FXML private VBox taskListPane;
    @FXML private ScrollPane ScrollPane;
    @FXML private Button reloadButton;
    @FXML private Button switchToNodeMap;
    @FXML private Button addEventButton;
    @FXML private Button goToTeamMenuButton;
    @FXML private Button logOutButton;
    @FXML private ImageView reloadImageView;
    @FXML private ImageView switchToNodeMapImageView;
    @FXML private ImageView addEventImageView;
    @FXML private ImageView goToTeamMenuImageView;
    @FXML private ImageView logOutImageView;
    @FXML private Text reloadText;
    @FXML private Text switchToNodeMapText;
    @FXML private Text addEventText;
    @FXML private Text goToTeamMenuText;
    @FXML private Text logOutText;
    private Scene scene;
    public static MainMenu instance;
    public MainMenu() {
        instance = this;
    }
    public static MainMenu getInstance() {
        return instance;
    }
    @FXML
    public void reloadTasks(){
        TaskManager.getInstance().FetchDataFromDatabase();
        refreshTaskList();

    }
    @FXML
    private void handleAddEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EventForm.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/styles/EventForm.css").toExternalForm());


            Stage stage = new Stage();
            stage.setTitle("新增事件");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();


            // 新增完任務後，刷新任務清單
            refreshTaskList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshTaskList() {
        taskListPane.getChildren().clear();
        List<Task> tasks = TaskManager.getInstance().getTaskList();
        for (Task task : tasks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskCell.fxml"));
                StackPane cell = loader.load();
                TaskCellController controller = loader.getController();
                controller.setTask(task);
                taskListPane.getChildren().add(cell);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @FXML
    private void switchToNodeMap(ActionEvent event) throws Exception {
        MainApplication.switchScene("NodeMapView.fxml");
    }

    @FXML
    private void goToTeamMenu() throws Exception{
        MainApplication.switchScene("TeamMenu.fxml");
    }

    @Override
    public void LoadEvent(Scene scene) {
        this.scene = scene;
        reloadTasks();
        DeBugConsole.log("LoadEvent " + getClass().getName());

    }

    @FXML
    public void  logOut() {
        try{
            MainApplication.switchScene("Login.fxml");
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @FXML
    private void initialize() {
        // 設置 ScrollPane 背景透明
        ScrollPane.setStyle("-fx-background-color: transparent;");

        // 設置 Viewport 背景透明
        Node viewport = ScrollPane.lookup(".viewport");
        if (viewport != null) {
            viewport.setStyle("-fx-background-color: transparent;");
        }

        // 設置 VBox 背景透明
        taskListPane.setStyle("-fx-background-color: transparent;");
        Image normalSaveImage = new Image(getClass().getResource("/images/TextBTN_Medium.png").toExternalForm());
        Image pressedSaveImage = new Image(getClass().getResource("/images/TextBTN_Medium_Pressed.png").toExternalForm());
        reloadButton.setOnMousePressed(e -> {
            reloadText.setTranslateY(reloadText.getTranslateY()+8);
            reloadImageView.setImage(pressedSaveImage);
        });
        reloadButton.setOnMouseReleased(e -> {
            reloadText.setTranslateY(reloadText.getTranslateY() - 8);
            reloadImageView.setImage(normalSaveImage);
        });
        switchToNodeMap.setOnMousePressed(e -> {
            switchToNodeMapText.setTranslateY(switchToNodeMapText.getTranslateY() + 8);
            switchToNodeMapImageView.setImage(pressedSaveImage);
        });
        switchToNodeMap.setOnMouseReleased(e -> {
            switchToNodeMapText.setTranslateY(switchToNodeMapText.getTranslateY() - 8);
            switchToNodeMapImageView.setImage(normalSaveImage);
        });
        addEventButton.setOnMousePressed(e -> {
            addEventText.setTranslateY(addEventText.getTranslateY() + 8);
            addEventImageView.setImage(pressedSaveImage);
        });
        addEventButton.setOnMouseReleased(e -> {
            addEventText.setTranslateY(addEventText.getTranslateY() - 8);
            addEventImageView.setImage(normalSaveImage);
        });
        goToTeamMenuButton.setOnMousePressed(e -> {
            goToTeamMenuText.setTranslateY(goToTeamMenuText.getTranslateY() + 8);
            goToTeamMenuImageView.setImage(pressedSaveImage);
        });
        goToTeamMenuButton.setOnMouseReleased(e -> {
            goToTeamMenuText.setTranslateY(goToTeamMenuText.getTranslateY() - 8);
            goToTeamMenuImageView.setImage(normalSaveImage);
        });
        logOutButton.setOnMousePressed(e -> {
            logOutText.setTranslateY(logOutText.getTranslateY() + 8);
            logOutImageView.setImage(pressedSaveImage);
        });
        logOutButton.setOnMouseReleased(e -> {
            logOutText.setTranslateY(logOutText.getTranslateY() - 8);
            logOutImageView.setImage(normalSaveImage);
        });
    }

    @Override
    public void UnloadEvent() {
        DeBugConsole.log("UnloadEvent " + getClass().getName());
    }

    @Override
    public void FreshEvent() {

    }
}