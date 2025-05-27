package org.Node;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import org.DeBugConsole;
import org.MainApplication;
import org.SceneInterface;
import org.Task.Task;
import org.Task.TaskManager;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeMapView implements SceneInterface {
    @FXML private Pane nodeMapPane;
    private Map<Integer, Parent> nodeViewMap = new HashMap<>();
    private Map<Integer, Point2D> nodePositionMap = new HashMap<>();
    private Map<Parent, Task> nodeToTaskMap = new HashMap<>();
    private static Parent draggingNode = null;
    private List<Task> tasks;
    @FXML
    public void initialize() {
        tasks = TaskManager.getInstance().getTaskList();
        double centerX = 400, centerY = 300, radius = 200;
        int n = tasks.size();
        for (int i = 0; i < n; i++) {
            Task task = tasks.get(i);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Node.fxml"));
            try{
                Parent node = loader.load();
                NodeController nodeController = loader.getController();


                nodeController.setTask(task);


                //TODO:佈局功能
                double angle = 2 * Math.PI * i / n;
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                node.setLayoutX(x);
                node.setLayoutY(y);
                nodePositionMap.put(task.getID(), new Point2D.Double(x, y));
                // 拖拽功能
                node.setOnMousePressed(event -> {
                    if(event.getButton() == MouseButton.PRIMARY){
                        node.setUserData(new double[]{event.getSceneX(), event.getSceneY(), node.getLayoutX(), node.getLayoutY()});
                        draggingNode = node;
                        node.toFront();
                        DeBugConsole.log("Node Dragged: " + node.getLayoutX() + "," + node.getLayoutY());
                        System.out.println("draggingNode: " + draggingNode + ", hash: " + System.identityHashCode(draggingNode));
                        System.out.println("targetNode: " + node + ", hash: " + System.identityHashCode(node));
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

                // 點擊事件
                node.setOnMouseClicked(event -> {
                    if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                        // 跳轉到 TaskInfo 畫面
                        try {
                            TaskManager.ShowInfo(task, (Stage) node.getScene().getWindow());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                nodeMapPane.getChildren().add(node);
                nodeViewMap.put(task.getID(), node);
                nodeToTaskMap.put(node, task);
                nodePositionMap.put(task.getID(), new Point2D.Double(x, y));

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        drawLines();
        nodeMapPane.setOnMouseReleased(event -> {
            if (draggingNode != null) {
                double mouseX = event.getScreenX();
                double mouseY = event.getScreenY();
                for (Parent node : nodeViewMap.values()) {
                    if (node == draggingNode) continue;
                    Bounds bounds = node.localToScreen(node.getBoundsInLocal());
                    if (bounds.contains(mouseX, mouseY)) {
                        onTaskDropped(draggingNode, node);
                        break;
                    }
                }
                draggingNode = null;
            }
        });

    }
    private void onTaskDropped(Parent draggedNode, Parent targetNode) {
        System.out.println("onTaskDropped called!");
        // 1. 取得對應的 Task
        Task draggedTask = nodeToTaskMap.get(draggedNode);
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
        for (Task task : tasks) {
            Integer parentId = task.getParentId();
            if (parentId != null && nodeViewMap.containsKey(parentId)) {
                Parent parentNode = nodeViewMap.get(parentId);
                Parent selfNode = nodeViewMap.get(task.getID());
                NodeConnection connection = new NodeConnection(parentNode, selfNode);
                connection.setStroke(Color.GRAY);
                connection.setStrokeWidth(2);
                nodeMapPane.getChildren().add(0, connection); // 放最底層
            }
        }
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
}