package org;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/taskant_userinfo";
        String user = "root";
        String password = "Z02280228z";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ 連線成功！");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
