package com.demo001.student;

import java.sql.*;
import java.util.Scanner;

/**
 * =====================================================================
 * 【demo001】最原始的 JDBC CRUD 演示 —— 学生管理系统
 * =====================================================================
 *
 * 【本 Demo 的教学目标】
 * 1. 让你第一次亲眼看到「Java 是如何操作 MySQL」的完整过程
 * 2. 理解 JDBC 里最核心的 5 个对象到底是干什么的：
 *      - DriverManager（驱动管理器）
 *      - Connection（数据库连接）
 *      - PreparedStatement（预编译 SQL 语句）
 *      - ResultSet（查询结果集）
 *      - 以及为什么要手动关闭资源
 * 3. 感受到「把所有代码写在一个类里」会带来多大的痛苦（为后面分层做铺垫）
 *
 * 【重要提醒】
 * - 这个版本**故意写得非常原始**，代码又臭又长、重复严重
 * - 实际开发中 99% 的公司都不会这么写
 * - 我们的目的是「先把底层原理看明白」，再慢慢学怎么写得优雅
 *
 * 【运行方式】
 *   mvn clean compile exec:java
 *
 * 作者：为你量身定制的教学 Demo
 * =====================================================================
 */
public class StudentJdbcDemo {

    // ====================== 数据库配置（硬编码） ======================
    // 注意：实际项目里这些信息绝对不能写死在代码里！
    // 后面我们会把它放到配置文件中。
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/student_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123456";

    // 用于接收用户输入
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     欢迎来到【学生管理系统 - 原始JDBC版】     ║");
        System.out.println("║   (demo001 - 最丑陋但最真实的起点)          ║");
        System.out.println("╚════════════════════════════════════════════╝");

        // 死循环 + 菜单
        while (true) {
            printMenu();

            System.out.print("请输入选项(1-5): ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        addStudent();       // 新增
                        break;
                    case "2":
                        listAllStudents();  // 查询全部
                        break;
                    case "3":
                        updateStudent();    // 修改
                        break;
                    case "4":
                        deleteStudent();    // 删除
                        break;
                    case "5":
                        System.out.println("感谢使用，再见！");
                        return;             // 退出程序
                    default:
                        System.out.println("输入有误，请输入 1~5 之间的数字！");
                }
            } catch (Exception e) {
                // 这里抓到异常就打印，防止程序崩溃
                System.out.println("【系统错误】" + e.getMessage());
                e.printStackTrace();
            }

            System.out.println(); // 打印空行，让界面好看点
        }
    }

    /**
     * 打印主菜单
     */
    private static void printMenu() {
        System.out.println("──────────────────── 功能菜单 ────────────────────");
        System.out.println("  1. 新增学生");
        System.out.println("  2. 查看所有学生");
        System.out.println("  3. 修改学生信息");
        System.out.println("  4. 删除学生");
        System.out.println("  5. 退出系统");
        System.out.println("──────────────────────────────────────────────────");
    }

    // =================================================================
    // 【新增学生】 - Create
    // =================================================================
    private static void addStudent() throws SQLException {
        System.out.println("\n【新增学生】");

        System.out.print("请输入姓名: ");
        String name = scanner.nextLine().trim();

        System.out.print("请输入年龄: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("请输入性别(0=女, 1=男): ");
        int gender = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("请输入邮箱(可为空): ");
        String email = scanner.nextLine().trim();

        System.out.print("请输入手机号(可为空): ");
        String phone = scanner.nextLine().trim();

        // ========== 真正的 JDBC 操作开始 ==========
        // 1. 获取数据库连接
        // 2. 编写 SQL（用 ? 占位符，防止 SQL 注入）
        // 3. 创建 PreparedStatement
        // 4. 设置参数
        // 5. 执行更新
        // 6. 关闭资源（必须！否则连接泄漏）

        // 使用 try-with-resources 自动关闭资源（Java 7+ 推荐写法）
        String sql = "INSERT INTO t_student(name, age, gender, email, phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 按顺序给 ? 赋值（第1个? 是 1）
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setInt(3, gender);
            ps.setString(4, email.isEmpty() ? null : email);
            ps.setString(5, phone.isEmpty() ? null : phone);

            // executeUpdate() 返回受影响的行数
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ 新增成功！共插入 " + rows + " 条记录");
            } else {
                System.out.println("❌ 新增失败");
            }
        }
        // try-with-resources 会自动调用 close()，不用我们手动写 finally
    }

    // =================================================================
    // 【查询所有学生】 - Read
    // =================================================================
    private static void listAllStudents() throws SQLException {
        System.out.println("\n【所有学生列表】");

        String sql = "SELECT id, name, age, gender, email, phone, create_time FROM t_student ORDER BY id";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // 打印表头
            System.out.println("────────────────────────────────────────────────────────────────────────────────────────");
            System.out.printf("%-5s %-10s %-5s %-6s %-25s %-15s %-20s\n",
                    "ID", "姓名", "年龄", "性别", "邮箱", "手机号", "创建时间");
            System.out.println("────────────────────────────────────────────────────────────────────────────────────────");

            boolean hasData = false;

            // ResultSet 就像一个「游标」，每调用一次 rs.next() 就往下移动一行
            while (rs.next()) {
                hasData = true;

                long id = rs.getLong("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                int gender = rs.getInt("gender");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                Timestamp createTime = rs.getTimestamp("create_time");

                String genderStr = gender == 1 ? "男" : (gender == 0 ? "女" : "未知");

                System.out.printf("%-5d %-10s %-5d %-6s %-25s %-15s %-20s\n",
                        id, name, age, genderStr,
                        email == null ? "" : email,
                        phone == null ? "" : phone,
                        createTime);
            }

            if (!hasData) {
                System.out.println("（当前没有任何学生数据）");
            }
            System.out.println("────────────────────────────────────────────────────────────────────────────────────────");
        }
    }

    // =================================================================
    // 【修改学生】 - Update
    // =================================================================
    private static void updateStudent() throws SQLException {
        System.out.println("\n【修改学生信息】");

        System.out.print("请输入要修改的学生ID: ");
        long id = Long.parseLong(scanner.nextLine().trim());

        // 先查一下这个人是否存在（简单演示）
        if (!existsById(id)) {
            System.out.println("❌ 系统中不存在 ID 为 " + id + " 的学生");
            return;
        }

        System.out.print("请输入新姓名(直接回车则不修改): ");
        String name = scanner.nextLine().trim();

        System.out.print("请输入新年龄(直接回车则不修改): ");
        String ageStr = scanner.nextLine().trim();

        System.out.print("请输入新性别(0女/1男，直接回车不修改): ");
        String genderStr = scanner.nextLine().trim();

        System.out.print("请输入新邮箱(直接回车不修改): ");
        String email = scanner.nextLine().trim();

        System.out.print("请输入新手机号(直接回车不修改): ");
        String phone = scanner.nextLine().trim();

        // 动态拼接 SET 子句（只修改用户填了值的字段）
        // 这种写法在实际项目中非常不推荐，这里只是为了演示「最原始」的做法
        StringBuilder sql = new StringBuilder("UPDATE t_student SET ");
        boolean needComma = false;

        if (!name.isEmpty()) {
            sql.append("name = ?");
            needComma = true;
        }
        if (!ageStr.isEmpty()) {
            if (needComma) sql.append(", ");
            sql.append("age = ?");
            needComma = true;
        }
        if (!genderStr.isEmpty()) {
            if (needComma) sql.append(", ");
            sql.append("gender = ?");
            needComma = true;
        }
        if (!email.isEmpty()) {
            if (needComma) sql.append(", ");
            sql.append("email = ?");
            needComma = true;
        }
        if (!phone.isEmpty()) {
            if (needComma) sql.append(", ");
            sql.append("phone = ?");
        }
        sql.append(" WHERE id = ?");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (!name.isEmpty()) ps.setString(paramIndex++, name);
            if (!ageStr.isEmpty()) ps.setInt(paramIndex++, Integer.parseInt(ageStr));
            if (!genderStr.isEmpty()) ps.setInt(paramIndex++, Integer.parseInt(genderStr));
            if (!email.isEmpty()) ps.setString(paramIndex++, email);
            if (!phone.isEmpty()) ps.setString(paramIndex++, phone);

            ps.setLong(paramIndex, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "✅ 修改成功！" : "❌ 修改失败");
        }
    }

    // =================================================================
    // 【删除学生】 - Delete
    // =================================================================
    private static void deleteStudent() throws SQLException {
        System.out.println("\n【删除学生】");

        System.out.print("请输入要删除的学生ID: ");
        long id = Long.parseLong(scanner.nextLine().trim());

        String sql = "DELETE FROM t_student WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ 删除成功！已删除 ID=" + id + " 的学生");
            } else {
                System.out.println("❌ 删除失败，可能是该学生不存在");
            }
        }
    }

    // ====================== 工具方法 ======================

    /**
     * 判断某个 ID 的学生是否存在
     */
    private static boolean existsById(long id) throws SQLException {
        String sql = "SELECT 1 FROM t_student WHERE id = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // 如果有结果就返回 true
            }
        }
    }
}
