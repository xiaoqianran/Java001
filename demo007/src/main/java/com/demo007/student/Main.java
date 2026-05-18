package com.demo007.student;

import com.demo007.student.controller.StudentController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * =====================================================================
 * 【demo007】程序入口 - 使用 Spring 容器启动
 * =====================================================================
 *
 * 重大变化：
 *
 * 以前：
 *   new StudentController().start();   // 手动 new 一大堆对象
 *
 * 现在：
 *   从 Spring 容器里把 Controller 拿出来（里面所有依赖都自动装配好了）
 *
 * 这才是真正“面向对象 + 依赖注入”的写法。
 * =====================================================================
 */
public class Main {

    public static void main(String[] args) {
        // 启动 Spring 容器
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 从容器中获取 Controller（所有依赖都已注入完毕）
        StudentController controller = context.getBean(StudentController.class);
        controller.start();

        // 关闭容器（实际项目中一般不用手动关）
        ((ClassPathXmlApplicationContext) context).close();
    }
}
