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

public class RegisterSystem {

    // 資料庫連接參數
    private static final String DB_URL = "jdbc:mysql://yamanote.proxy.rlwy.net:44528/taskant_userinfo";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "zrKLjtYqVNzwFAVvMtklGAWgKlGHFPhb";

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

        if (username.equals("") || password.equals("") || reEnterPassword.equals("")) {
            showRegisterStatus.setText("Please enter all fields.");
            showRegisterStatus.setTextFill(Color.RED);
        } else if (!password.equals(reEnterPassword)) {
            showRegisterStatus.setText("Passwords do not match, please try again.");
            showRegisterStatus.setTextFill(Color.RED);
        } else {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // 檢查使用者名稱是否已存在
                String checkUserQuery = "SELECT COUNT(*) FROM user WHERE UserAccount = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkUserQuery)) {
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        showRegisterStatus.setText("Username already exists.");
                        showRegisterStatus.setTextFill(Color.RED);
                        return;
                    }
                }

                // 插入新使用者
                String insertUserQuery = "INSERT INTO user (UserAccount, UserPassword) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertUserQuery)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);
                    insertStmt.executeUpdate();
                    showRegisterStatus.setText("Register successful!");
                    showRegisterStatus.setTextFill(Color.GREEN);
                }
            } catch (SQLException e) {
                showRegisterStatus.setText("Database error: " + e.getMessage());
                showRegisterStatus.setTextFill(Color.RED);
            }
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

