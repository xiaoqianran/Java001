package com.demo003.student;

import com.demo003.student.controller.StudentController;

/**
 * =====================================================================
 * demo002 程序入口
 * =====================================================================
 *
 * 教学意义：
 *   - Main 只负责启动「控制器」
 *   - 真正的业务流程都在 Controller -> Service -> Mapper 里流转
 *
 * 和 demo001 的区别：
 *   demo001 的 main 方法里写了 200 行菜单 + JDBC
 *   demo002 的 main 只有 3 行！
 * =====================================================================
 */
public class Main {

    public static void main(String[] args) {
        new StudentController().start();
    }
}
