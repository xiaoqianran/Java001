-- ============================================================
-- Mall 电商系统初始化脚本
-- ============================================================
-- 策略：功能推进到哪里，表就加到哪里。
-- 当前阶段：Phase 1 Step 1 - 用户领域基础（sys_user）
-- ============================================================

USE mall_db;

-- ============================================================
-- sys_user - 用户账号表（电商系统核心用户表）
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username      VARCHAR(50)  NOT NULL UNIQUE      COMMENT '登录用户名（唯一）',
    password      VARCHAR(100) NOT NULL             COMMENT 'BCrypt 加密后的密码',
    nickname      VARCHAR(50)                       COMMENT '昵称/展示名',
    real_name     VARCHAR(50)                       COMMENT '真实姓名',
    phone         VARCHAR(20)  UNIQUE               COMMENT '手机号（可用于登录或通知）',
    email         VARCHAR(100)                      COMMENT '邮箱',
    avatar        VARCHAR(255)                      COMMENT '头像URL',
    role          TINYINT      NOT NULL DEFAULT 3   COMMENT '角色：1=管理员, 2=商家, 3=普通买家',
    status        TINYINT      NOT NULL DEFAULT 1   COMMENT '状态：0=禁用, 1=正常',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      DEFAULT 0            COMMENT '逻辑删除：0=未删, 1=已删',
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账号表';

-- 插入一个初始管理员账号（密码：123456，BCrypt 后）
-- 注意：实际项目中密码应通过注册接口生成，这里仅为开发方便
INSERT INTO sys_user (username, password, nickname, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '管理员', 1, 1);

SELECT 'sys_user 表创建完成（Phase 1 Step 1）' AS message;
