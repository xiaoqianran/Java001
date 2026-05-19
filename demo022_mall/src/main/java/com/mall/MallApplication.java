package com.mall;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * 【demo021_mall - 项目入口】小型电商系统主启动类（Phase 1-3 已完成）
 * =====================================================================
 *
 * 项目定位：
 *   这是「Java 教学系列」从 demo001~demo020（学生管理系统）进化而来的
 *   真实业务项目起点。后续将以 demo021_mall → demo022_mall → ... → demo030+
 *   的方式，循序渐进地构建完整的小型电商系统。
 *
 * 当前状态（demo021_mall）：
 *   - Phase 1：用户注册 + JWT 登录 + Spring Security 认证授权（已完成）
 *   - Phase 2：商品域（Category / SPU / SKU + 乐观锁库存）（已完成）
 *   - Phase 3：购物车完整功能（已完成）
 *
 * 教学原则：
 *   每一步只做一个重点变化，严格基于上一个 demo 的代码进行小步升级。
 * =====================================================================
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.mall.**.mapper")
public class MallApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
        log.info("========================================");
        log.info("   demo021_mall 小型电商系统启动成功");
        log.info("   当前已完成 Phase 1-3：用户认证 + 商品域 + 购物车");
        log.info("========================================");
    }
}
