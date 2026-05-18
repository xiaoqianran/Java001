package com.demo003.student.controller;

import com.demo003.student.entity.Student;
import com.demo003.student.service.StudentService;
import com.demo003.student.service.impl.StudentServiceImpl;

import java.util.List;
import java.util.Scanner;

/**
 * =====================================================================
 * 【Controller 层】控制台交互层（模拟 Web Controller）
 * =====================================================================
 *
 * 在真实 Web 项目里，Controller 接收 HTTP 请求、调用 Service、返回 JSON。
 * 这里我们用 Scanner 模拟「用户输入」，作用是一样的：
 *   - 接收用户指令
 *   - 调用 Service 完成业务
 *   - 把结果展示给用户
 *
 * 重点教学：
 *   - Controller 应该「很薄」，只负责交互和参数接收
 *   - 真正的业务逻辑和数据库操作都不应该出现在 Controller 里
 * =====================================================================
 */
public class StudentController {

    private final StudentService studentService = new StudentServiceImpl();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   学生管理系统 v2.0（分层架构版 - 无Lombok）    ║");
        System.out.println("║         demo002 - 传统三层架构教学              ║");
        System.out.println("╚════════════════════════════════════════════════╝");

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": addStudent(); break;
                    case "2": listStudents(); break;
                    case "3": updateStudent(); break;
                    case "4": deleteStudent(); break;
                    case "5":
                        System.out.println("感谢使用，再见！");
                        return;
                    default:
                        System.out.println("请输入 1-5 之间的数字");
                }
            } catch (Exception e) {
                System.out.println("【错误】" + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n────────────────── 功能菜单 ──────────────────");
        System.out.println("  1. 新增学生");
        System.out.println("  2. 查看所有学生");
        System.out.println("  3. 修改学生信息");
        System.out.println("  4. 删除学生");
        System.out.println("  5. 退出");
        System.out.println("──────────────────────────────────────────────");
        System.out.print("请选择: ");
    }

    private void addStudent() {
        System.out.println("\n【新增学生】");
        Student s = new Student();

        System.out.print("姓名: ");
        s.setName(scanner.nextLine().trim());

        System.out.print("年龄: ");
        s.setAge(Integer.parseInt(scanner.nextLine().trim()));

        System.out.print("性别(0女/1男): ");
        s.setGender(Integer.parseInt(scanner.nextLine().trim()));

        System.out.print("邮箱: ");
        s.setEmail(scanner.nextLine().trim());

        System.out.print("手机号: ");
        s.setPhone(scanner.nextLine().trim());

        boolean success = studentService.addStudent(s);
        System.out.println(success ? "✅ 新增成功" : "❌ 新增失败");
    }

    private void listStudents() {
        System.out.println("\n【学生列表】");
        List<Student> list = studentService.getAllStudents();

        if (list.isEmpty()) {
            System.out.println("暂无数据");
            return;
        }

        System.out.println("────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-4s %-8s %-4s %-4s %-22s %-13s\n", "ID", "姓名", "年龄", "性别", "邮箱", "手机号");
        System.out.println("────────────────────────────────────────────────────────────────────────");

        for (Student s : list) {
            String gender = s.getGender() == 1 ? "男" : (s.getGender() == 0 ? "女" : "未知");
            System.out.printf("%-4d %-8s %-4d %-4s %-22s %-13s\n",
                    s.getId(), s.getName(), s.getAge(), gender,
                    s.getEmail() == null ? "" : s.getEmail(),
                    s.getPhone() == null ? "" : s.getPhone());
        }
    }

    private void updateStudent() {
        System.out.print("\n请输入要修改的学生ID: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        Student s = studentService.getStudentById(id);
        if (s == null) {
            System.out.println("学生不存在");
            return;
        }

        System.out.println("当前信息: " + s);

        System.out.print("新姓名(回车跳过): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) s.setName(name);

        System.out.print("新年龄(回车跳过): ");
        String ageStr = scanner.nextLine().trim();
        if (!ageStr.isEmpty()) s.setAge(Integer.parseInt(ageStr));

        // 其他字段类似处理...

        boolean success = studentService.updateStudent(s);
        System.out.println(success ? "✅ 修改成功" : "❌ 修改失败");
    }

    private void deleteStudent() {
        System.out.print("\n请输入要删除的学生ID: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        boolean success = studentService.deleteStudent(id);
        System.out.println(success ? "✅ 删除成功" : "❌ 删除失败");
    }
}
