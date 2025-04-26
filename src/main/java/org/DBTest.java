package org;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBTest {
    public static void main(String[] args) {
        final String DB_URL = "jdbc:mysql://yamanote.proxy.rlwy.net:44528/taskant_userinfo";
        final String DB_USER = "root";
        final String DB_PASSWORD = "zrKLjtYqVNzwFAVvMtklGAWgKlGHFPhb";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ 連線成功！");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
