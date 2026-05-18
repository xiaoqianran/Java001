package com.demo019.student;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo019】Docker + Docker Compose 一键部署
 * =====================================================================
 *
 * 核心变化：
 * - 新增 Dockerfile（多阶段构建，推荐生产做法）
 * - 新增 docker-compose.yml（编排 app + mysql + redis）
 * - 支持环境变量配置（便于不同环境切换）
 * - 完整的一键部署能力
 * =====================================================================
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.demo019.student.mapper")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        log.info("=== 学生管理系统 REST API 已启动 ===");
        log.info("接口地址示例：http://localhost:8080/students");
    }
}
