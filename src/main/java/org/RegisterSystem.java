package org;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class RegisterSystem {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField reEnterField;

    @FXML
    private Label showRegisterStatus;

    @FXML
    private void registerApply() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String reEnterPassword = reEnterField.getText();
        if(username.equals("") || password.equals("") || reEnterPassword.equals("")) {//todo:尚未測試輸入空格的情況
            showRegisterStatus.setText("Please enter all fields.");
            showRegisterStatus.setTextFill(Color.RED);  // 設置文字顏色為紅色
        }
        //todo:尚未製作偵測username重複的狀況
        else if (!password.equals(reEnterPassword)) {
            showRegisterStatus.setText("Passwords do not match, please try again.");
            showRegisterStatus.setTextFill(Color.RED);  // 設置文字顏色為紅色
            // 這裡可以轉換場景或跳轉到主頁
        } else {
            showRegisterStatus.setText("Register successful!");
            showRegisterStatus.setTextFill(Color.GREEN);  // 設置文字顏色為紅色
        }
    }

    @FXML
    private void cancel() {
        try {
            MainApplication.switchScene("Login.fxml");
        } catch (Exception e) {
            showRegisterStatus.setText("Error opening register page");
            showRegisterStatus.setTextFill(Color.RED);
        }
    }

}
