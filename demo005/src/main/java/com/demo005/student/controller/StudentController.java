package com.demo005.student.controller;

import com.demo005.student.entity.Student;
import com.demo005.student.service.StudentService;
import com.demo005.student.service.impl.StudentServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

/**
 * =====================================================================
 * 【demo004】Controller —— 在 Lombok 基础上增加 @Slf4j
 * =====================================================================
 *
 * 本版本的改动（严格控制在一件事）：
 *   - 加入 @Slf4j
 *   - 使用 log.info / log.error 替代部分 System.out
 *   - 在 addStudent 方法中使用了 Student.builder()（这是顺手带的 Builder 用法）
 *
 * 重点教学：当项目已经有了 Lombok 后，再增加日志功能是多么自然。
 * =====================================================================
 */
@Slf4j
public class StudentController {

    private final StudentService studentService = new StudentServiceImpl();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        log.info("学生管理系统启动（demo004 - Lombok 全面版）");

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
                        log.info("用户选择退出");
                        return;
                    default:
                        System.out.println("请输入 1-5 之间的数字");
                }
            } catch (Exception e) {
                log.error("操作异常", e);
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

        // 使用 Lombok @Builder 创建对象，可读性极强
        Student s = Student.builder()
                .name(readInput("姓名"))
                .age(Integer.parseInt(readInput("年龄")))
                .gender(Integer.parseInt(readInput("性别(0女/1男)")))
                .email(readInput("邮箱(可空)"))
                .phone(readInput("手机号(可空)"))
                .build();

        boolean success = studentService.addStudent(s);
        System.out.println(success ? "✅ 新增成功" : "❌ 新增失败");
    }

    private String readInput(String tip) {
        System.out.print(tip + ": ");
        return scanner.nextLine().trim();
    }

    private void listStudents() {
        log.debug("执行查询所有学生操作");
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

        // 其他字段省略...

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
