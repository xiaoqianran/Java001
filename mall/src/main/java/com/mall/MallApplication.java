package com.mall;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【mall - 项目入口 / 对应原 demo021 定位】小型电商系统主启动类
 * =====================================================================
 *
 * 项目定位：
 *   这是从学生管理系统（demo001~demo020）进化而来的真正业务项目。
 *   我们不再用“学生”做例子，而是直接做有实际价值的「小型电商系统」。
 *
 * 当前阶段（Phase 1）教学主线：
 *   Step 1 ~ Step 4 已经完成：用户 + 注册 + 登录 + JWT 完整认证链路
 *
 * 注释规范（按用户要求）：
 *   - 只在教学价值高的核心文件上加详细大注释
 *   - 关键变动行会用行内注释重点标注
 *   - 每个 Phase 的重要 Step 都会清晰说明“这一步到底改了什么”
 * =====================================================================
 */
@Slf4j
@SpringBootApplication
@MapperScan({"com.mall.module.*.mapper", "com.mall.mapper"})
public class MallApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
        log.info("========================================");
        log.info("   Mall 小型电商系统启动成功");
        log.info("   当前已完成 Phase 1：用户注册 + JWT 登录 + 认证过滤器");
        log.info("========================================");
    }
}
