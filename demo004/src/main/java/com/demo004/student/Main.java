package com.demo004.student;

import com.demo004.student.controller.StudentController;

/**
 * =====================================================================
 * demo004 程序入口
 * =====================================================================
 *
 * 教学意义：
 *   - Main 依然只有 3 行
 *   - 所有复杂性都被 Lombok + 分层封装得非常清晰
 * =====================================================================
 */
public class Main {

    public static void main(String[] args) {
        new StudentController().start();
    }
}
