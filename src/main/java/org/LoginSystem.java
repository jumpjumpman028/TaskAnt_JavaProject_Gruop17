package org;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class LoginSystem {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;  // 用來顯示登入結果的 Label

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if ("admin".equals(username) && "1234".equals(password)) {
            statusLabel.setText("Login successful!");
            statusLabel.setTextFill(Color.GREEN);  // 設置文字顏色為綠色
            // 這裡可以轉換場景或跳轉到主頁
        } else {
            statusLabel.setText("Invalid credentials");
            statusLabel.setTextFill(Color.RED);  // 設置文字顏色為紅色
        }
    }
}
