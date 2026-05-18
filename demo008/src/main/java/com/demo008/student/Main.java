package com.demo008.student;

import com.demo008.student.controller.StudentController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo008】Spring Boot 启动入口（最终版）
 * =====================================================================
 *
 * 重大升级：
 *
 * - 使用 @SpringBootApplication 替代手动启动 Spring 容器
 * - 完全告别 applicationContext.xml
 * - 配置全部移到 application.yml，由 Spring Boot 自动完成
 * - MyBatis 由 mybatis-spring-boot-starter 自动集成
 *
 * 运行方式：
 *   mvn spring-boot:run
 *   或
 *   java -jar target/xxx.jar
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
