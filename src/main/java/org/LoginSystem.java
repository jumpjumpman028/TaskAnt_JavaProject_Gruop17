package org;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginSystem {

    // 資料庫連接參數
    private static final String DB_URL = "jdbc:mysql://yamanote.proxy.rlwy.net:44528/taskant_userinfo";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "zrKLjtYqVNzwFAVvMtklGAWgKlGHFPhb";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String loginQuery = "SELECT COUNT(*) FROM user WHERE UserAccount = ? AND UserPassword = ?";
            try (PreparedStatement loginStmt = connection.prepareStatement(loginQuery)) {
                loginStmt.setString(1, username);
                loginStmt.setString(2, password);
                ResultSet rs = loginStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    statusLabel.setText("Login successful!");
                    statusLabel.setTextFill(Color.GREEN);
                    // 這裡可以轉換場景或跳轉到主頁
                    try {
                        MainApplication.switchScene("Menu.fxml");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    statusLabel.setText("Invalid credentials");
                    statusLabel.setTextFill(Color.RED);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Database error: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void onRegister() {
        try {
            MainApplication.switchScene("Register.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error opening register page");
            statusLabel.setTextFill(Color.RED);
        }
    }
}

