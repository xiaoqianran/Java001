package com.demo012.student;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo012】Spring Boot REST API 启动入口
 * =====================================================================
 *
 * 重大变化：
 * - 不再实现 CommandLineRunner
 * - 不再调用控制台菜单
 * - Spring Boot 启动后直接对外提供 HTTP 接口
 *
 * 访问示例：
 *   GET  http://localhost:8080/students
 *   POST http://localhost:8080/students
 * =====================================================================
 */
@Slf4j
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        log.info("=== 学生管理系统 REST API 已启动 ===");
        log.info("接口地址示例：http://localhost:8080/students");
    }
}
