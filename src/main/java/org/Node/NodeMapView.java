package org.Node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.DeBugConsole;
import org.MainApplication;
import org.SceneInterface;
import org.Task.Task;
import org.Task.TaskCellController;
import org.Task.TaskManager;

import java.awt.geom.Point2D;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeMapView implements SceneInterface {

    public static NodeMapView instance;
    public static NodeMapView GetInstance(){return instance;}

    @FXML private Pane nodeMapPane;
    @FXML private VBox unassignedBox;
    @FXML private Group nodeGroup;
    private double lastMouseX, lastMouseY;
    private ObservableList<Task> unassignedTasks = FXCollections.observableArrayList();
    private Map<Integer, Parent> nodeViewMap = new HashMap<>();
    private Map<Integer, Point2D> nodePositionMap = new HashMap<>();
    private Map<Parent, Task> nodeToTaskMap = new HashMap<>();
    private static Parent draggingNode = null;
    private List<Task> tasks;
    @FXML
    public void initialize() {
        tasks = TaskManager.getInstance().getTaskList();
        GetUnassignedBox();
        //setupCanvasDragDrop();
        drawLines();
        refreshUnassignedBox();
        setupNodeMapPaneMouseReleased();
    }
    private void onTaskDropped(Parent draggedNode, Parent targetNode) {

        System.out.println("onTaskDropped called!");
        // 1. 取得對應的 Task
        Task draggedTask = nodeToTaskMap.get(draggedNode);
        DeBugConsole.log("使用nodeTask");
        Task targetTask = nodeToTaskMap.get(targetNode);

        // 2. 設定 parentId
        draggedTask.setParentId(targetTask.getID());

        // 3. 調整 draggedNode 的座標（排在 targetNode 旁邊）
        double targetX = targetNode.getLayoutX();
        double targetY = targetNode.getLayoutY();
        double offset = 120; // 你可以自訂偏移量
        draggedNode.setLayoutX(targetX + offset);
        draggedNode.setLayoutY(targetY);

        // 4. 更新 position map
        nodePositionMap.put(draggedTask.getID(), new Point2D.Double(targetX + offset, targetY));
        drawLines();
    }

    private void drawLines() {
        nodeGroup.getChildren().removeIf(n -> n instanceof NodeConnection);

        for (Task task : tasks) {
            Integer parentId = task.getParentId();
            // 如果 parentId 存在，且 parentId 對應的節點在 nodeViewMap
            if (parentId != null && parentId > 0 && nodeViewMap.containsKey(parentId)) {
                Parent parentNode = nodeViewMap.get(parentId);
                Parent selfNode = nodeViewMap.get(task.getID());
                if (selfNode != null && parentNode != null) {
                    NodeConnection connection = new NodeConnection(parentNode, selfNode);
                    if(nodeToTaskMap.get(parentNode).getStatus() == Task.Status.COMPLETED) {
                        connection.setStroke(Color.GREEN);
                    }
                    else connection.setStroke(Color.GRAY);
                    connection.setStrokeWidth(2);
                    nodeGroup.getChildren().add(1, connection); // 放最底層
                }
            } else if (parentId != null && parentId != 0) {
                // parentId 對應的節點不在畫布上，設為未分配
                task.setParentId(0); // 或改成 null
            }
        }
    }

    private void GetUnassignedBox(){
        for (Task task : tasks) {
            if (task.getParentId() == null) { // 或用你的判斷條件
                unassignedTasks.add(task);
            }
        }
    }
    private void refreshUnassignedBox() {
        unassignedBox.getChildren().removeIf(n -> n instanceof StackPane); // 移除舊的 TaskCell
        for (Task task : unassignedTasks) {
            try {
                URL fxmlUrl = getClass().getResource("/org/TaskCell.fxml");
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                StackPane taskCell = loader.load();
                TaskCellController controller = loader.getController();
                controller.setTask(task);

                // 設定滑鼠拖曳
                setupUnassignedTaskDrag(taskCell, task);

                unassignedBox.getChildren().add(taskCell);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupUnassignedTaskDrag(StackPane taskCell, Task task) {
        // 在 initialize() 或畫面建立後呼叫
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(nodeMapPane.widthProperty());
        clip.heightProperty().bind(nodeMapPane.heightProperty());
        nodeMapPane.setClip(clip);
        taskCell.setOnMousePressed(event -> {
            taskCell.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
            draggingNode = taskCell;
            taskCell.toFront();
        });

        taskCell.setOnMouseDragged(event -> {
            double[] data = (double[]) taskCell.getUserData();
            double dx = event.getSceneX() - data[0];
            double dy = event.getSceneY() - data[1];
            taskCell.setTranslateX(dx);
            taskCell.setTranslateY(dy);
        });

        taskCell.setOnMouseReleased(event -> {
            // 拖曳結束時判斷是否在 Pane 上
            Bounds paneBounds = nodeMapPane.localToScene(nodeMapPane.getBoundsInLocal());
            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();
            if (paneBounds.contains(sceneX, sceneY)) {
                // 從 unassigned 移到畫布
                unassignedTasks.remove(task);
                refreshUnassignedBox();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Node.fxml"));
                    Parent node = loader.load();
                    NodeController nodeController = loader.getController();
                    nodeController.setTask(task);

                    // 設定座標
                    javafx.geometry.Point2D localPoint = nodeGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
                    //double localX = nodeMapPane.sceneToLocal(sceneX, sceneY).getX();
                    //double localY = nodeMapPane.sceneToLocal(sceneX, sceneY).getY();
                    node.setLayoutX(localPoint.getX());
                    node.setLayoutY(localPoint.getY());
                    nodeGroup.getChildren().add(node);

                    // 設 parentId
                    task.setParentId(0);

                    // 註冊拖曳事件
                    setupNodeEvents(node, task);
                    nodeViewMap.put(task.getID(), node);
                    nodeToTaskMap.put(node, task);
                    DeBugConsole.log("獲得nodeTask");
                    nodePositionMap.put(task.getID(), new Point2D.Double(localPoint.getX(), localPoint.getY()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                drawLines();
            }
            // 歸零
            taskCell.setTranslateX(0);
            taskCell.setTranslateY(0);
        });
    }
    private void setupNodeEvents(Parent node, Task task) {
        node.setOnMousePressed(event -> {
            if(event.getButton() == MouseButton.PRIMARY){
                node.setUserData(new double[]{event.getSceneX(), event.getSceneY(), node.getLayoutX(), node.getLayoutY()});
                draggingNode = node;
                node.toFront();
            }
        });
        node.setOnMouseDragged(event -> {
            if(event.getButton() == MouseButton.PRIMARY){
                double[] data = (double[]) node.getUserData();
                double dx = event.getSceneX() - data[0];
                double dy = event.getSceneY() - data[1];
                node.setLayoutX(data[2] + dx);
                node.setLayoutY(data[3] + dy);
            }
        });
        node.setOnMouseReleased(event -> {
            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();
            Bounds vboxBounds = unassignedBox.localToScene(unassignedBox.getBoundsInLocal());
            Bounds paneBounds = nodeMapPane.localToScene(nodeMapPane.getBoundsInLocal());

            if (vboxBounds.contains(sceneX, sceneY)) {
                // 拖回未分配區
                nodeGroup.getChildren().remove(node);
                nodeViewMap.remove(task.getID());
                nodeToTaskMap.remove(node);
                nodePositionMap.remove(task.getID());
                task.setParentId(null);
                if (!unassignedTasks.contains(task)) {
                    unassignedTasks.add(task);
                }
                refreshUnassignedBox();
                drawLines();
            } else if (paneBounds.contains(sceneX, sceneY)) {
                // 只是在畫布上移動
                nodePositionMap.put(task.getID(), new Point2D.Double(node.getLayoutX(), node.getLayoutY()));
                drawLines();
            }
        });
        node.setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                try {
                    TaskManager.ShowInfo(task, (Stage) node.getScene().getWindow());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        nodeMapPane.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) { // 右鍵
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        nodeMapPane.setOnMouseDragged(event -> {
            if (event.isSecondaryButtonDown()) { // 右鍵
                double dx = event.getSceneX() - lastMouseX;
                double dy = event.getSceneY() - lastMouseY;
                nodeGroup.setLayoutX(nodeGroup.getLayoutX() + dx);
                nodeGroup.setLayoutY(nodeGroup.getLayoutY() + dy);
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });
    }
    private void setupNodeMapPaneMouseReleased() {

        nodeMapPane.setOnMouseReleased(event -> {
            if (draggingNode != null) {
                double mouseX = event.getScreenX();
                double mouseY = event.getScreenY();
                for (Parent node : nodeViewMap.values()) {
                    if (node == draggingNode) continue;
                    Bounds bounds = node.localToScreen(node.getBoundsInLocal());
                    if (bounds.contains(mouseX, mouseY)) {
                        onTaskDropped(draggingNode, node);
                        System.out.println("Trigger onTaskDropped: " + node);

                        break;
                    }
                }
                draggingNode = null;
            }
        });
    }

    private Task findUnassignedTaskById(int id) {
        for (Task task : unassignedTasks) {
            if (task.getID() == id) return task;
        }
        return null;
    }
    @FXML
    private void reFresh() throws Exception{
        refreshScene();
    }
    private void refreshScene(){
        DeBugConsole.log("Refreshing scene");
        refreshUnassignedBox();
        drawLines();
    }
    // 返回按鈕事件
    @FXML
    private void onBack() throws Exception {
        MainApplication.switchScene("WaterTest.fxml");
    }

    @Override
    public void LoadEvent() {

    }

    @Override
    public void UnloadEvent() {

    }

    @Override
    public void FreshEvent() {
        refreshScene();
    }
}