package org;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterSystem implements SceneInterface{

    @Override
    public void LoadEvent(Scene scene) {

    }

    @Override
    public void UnloadEvent() {

    }

    @Override
    public void FreshEvent() {

    }

    // 資料庫連接參數

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
            try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection()) {
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
                    PauseTransition pause = new PauseTransition(Duration.seconds(1)); // 2秒的延遲
                    pause.setOnFinished(event -> {
                        try {
                            MainApplication.switchScene("Login.fxml");
                        } catch (Exception ex) {
                            showRegisterStatus.setText("Error opening register page");
                            showRegisterStatus.setTextFill(Color.RED);
                        }
                    });
                    pause.play();
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

