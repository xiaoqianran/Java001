package com.demo018.student;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo018】JWT + Spring Security 基础认证
 * =====================================================================
 *
 * 核心变化：
 * - 引入 Spring Security + JWT 实现无状态认证
 * - 提供 /auth/login 接口签发 JWT
 * - 通过 JwtAuthenticationFilter 拦截所有请求并校验 Token
 * - 基于 sys_user 表实现真实用户认证
 * =====================================================================
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.demo018.student.mapper")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        log.info("=== 学生管理系统 REST API 已启动 ===");
        log.info("接口地址示例：http://localhost:8080/students");
    }
}
