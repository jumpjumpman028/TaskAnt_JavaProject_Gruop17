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
import java.time.LocalDate;

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
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // 獲取 userID 和其他資訊
                int userID = resultSet.getInt("user_id");
                LocalDate createDate = resultSet.getDate("create_date").toLocalDate();

                // 設定目前登入的使用者
                UserInfo currentUser = new UserInfo(username, createDate, userID);
                UserInfo.setCurrentUser(currentUser);

                // 載入該使用者的任務
                //todo:TaskManager.getInstance().loadTasksForUser(userID);

                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("登入成功！");
            } else {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("帳號或密碼錯誤！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("登入時發生錯誤！");
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

