package org;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WaterTest {

    @FXML
    private void handleAddEvent(ActionEvent event) {
        try {
            // 載入 EventForm.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EventForm.fxml"));
            Parent root = loader.load();

            // 新建一個 Stage (視窗)
            Stage stage = new Stage();
            stage.setTitle("新增事件");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // 阻擋其他視窗操作，直到此視窗關閉
            stage.showAndWait(); // 等待視窗關閉
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}