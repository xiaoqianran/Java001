package com.demo017.student;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo017】Redis 缓存集成（@Cacheable + 缓存穿透处理）
 * =====================================================================
 *
 * 核心变化：
 * - 集成 Redis + Spring Boot Cache
 * - 使用 @Cacheable、@CacheEvict、@CachePut 注解
 * - 演示缓存穿透、缓存击穿的处理方案（空值缓存 + 随机 TTL）
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
