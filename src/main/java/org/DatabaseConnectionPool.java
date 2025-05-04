package org;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConnectionPool {
    private static HikariDataSource dataSource;

    // 初始化連線池
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://yamanote.proxy.rlwy.net:44528/taskant_userinfo"); // 資料庫 URL
        config.setUsername("root"); // 資料庫使用者名稱
        config.setPassword("zrKLjtYqVNzwFAVvMtklGAWgKlGHFPhb"); // 資料庫密碼

        // 可選配置（根據需求調整）
        config.setMaximumPoolSize(10); // 最大連線數
        config.setMinimumIdle(2); // 最小空閒連線數
        config.setIdleTimeout(30000); // 空閒連線超時時間（毫秒）
        config.setMaxLifetime(1800000); // 連線最大存活時間（毫秒）
        config.setConnectionTimeout(30000); // 取得連線的超時時間（毫秒）

        dataSource = new HikariDataSource(config);
    }

    // 獲取資料來源
    public static DataSource getDataSource() {
        return dataSource;
    }

    // 關閉連線池（在應用程式結束時調用）
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
