package com.demo015.student;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo015】Spring Boot REST API + 事务管理
 * =====================================================================
 *
 * 演示事务回滚效果。
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
