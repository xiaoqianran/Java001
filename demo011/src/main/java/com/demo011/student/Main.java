package com.demo011.student;

import com.demo011.student.controller.StudentController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo010】Spring Boot + MyBatis-Plus Service 封装
 * =====================================================================
 *
 * 相比 demo009：
 * - Service 层也继承了 ServiceImpl
 * - 代码进一步精简，业务方法更聚焦
 *
 * 运行方式不变。
 * =====================================================================
 */
@Slf4j
@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private StudentController studentController;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Spring Boot 应用启动完成，进入学生管理系统...");
        studentController.start();
    }
}
