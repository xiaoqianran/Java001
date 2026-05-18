package com.demo016.student;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo016】统一日志 + 请求响应日志（MDC + 拦截器）
 * =====================================================================
 *
 * 核心变化：
 * - 引入 MDC（Mapped Diagnostic Context）传递 traceId
 * - 使用 HandlerInterceptor + OncePerRequestFilter 记录请求/响应日志
 * - logback-spring.xml 美化日志格式，包含 traceId、耗时、URI 等
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
