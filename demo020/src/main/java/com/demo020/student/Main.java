package com.demo020.student;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo020】完整项目重构（模块划分 + 代码规范）
 * =====================================================================
 *
 * 本次重构目标：
 * - 按领域拆分模块（user、product、order、common 等）
 * - 引入 DTO + VO，彻底分离 Entity 与接口
 * - 统一异常、统一返回、统一日志规范
 * - 目录结构更清晰，符合真实项目最佳实践
 * =====================================================================
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.demo020.student.mapper")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        log.info("=== 学生管理系统 REST API 已启动 ===");
        log.info("接口地址示例：http://localhost:8080/students");
    }
}
