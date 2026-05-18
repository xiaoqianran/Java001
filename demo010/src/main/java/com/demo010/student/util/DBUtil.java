package com.demo010.student.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * =====================================================================
 * 数据库连接工具类
 * =====================================================================
 *
 * 作用：
 *   把「获取数据库连接」这个重复的逻辑抽出来，避免到处写 JDBC_URL
 *
 * 注意：
 *   - 这个版本仍然是「每次用都新建连接」，性能不好
 *   - 后面学了连接池（HikariCP / Druid）后会彻底改造这里
 * =====================================================================
 */
public class DBUtil {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/student_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123456";

    /**
     * 获取数据库连接
     * 调用方用完后必须自己关闭 Connection！
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    /**
     * 关闭连接（防御式编程）
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
