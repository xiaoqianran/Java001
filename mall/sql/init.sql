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

-- ============================================================
-- Phase 2 Step 1: 商品分类表（product_category）
-- ============================================================
DROP TABLE IF EXISTS product_category;
CREATE TABLE product_category (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    parent_id     BIGINT       NOT NULL DEFAULT 0   COMMENT '父分类ID，0表示顶级分类',
    name          VARCHAR(50)  NOT NULL             COMMENT '分类名称',
    level         TINYINT      NOT NULL DEFAULT 1   COMMENT '分类层级：1一级，2二级，3三级',
    sort          INT          DEFAULT 0            COMMENT '排序字段',
    icon          VARCHAR(255)                      COMMENT '分类图标URL',
    description   VARCHAR(500)                      COMMENT '分类描述',
    status        TINYINT      DEFAULT 1            COMMENT '状态：0=禁用，1=启用',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      DEFAULT 0            COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 示例数据
INSERT INTO product_category (parent_id, name, level, sort) VALUES
(0, '手机数码', 1, 1),
(0, '电脑办公', 1, 2),
(0, '家用电器', 1, 3);

SELECT 'product_category 表创建完成（Phase 2 Step 1）' AS message;

-- ============================================================
-- Phase 2 Step 3: SPU（标准产品单元）表
-- ============================================================
DROP TABLE IF EXISTS product_spu;
CREATE TABLE product_spu (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'SPU ID',
    category_id   BIGINT       NOT NULL             COMMENT '所属分类ID',
    name          VARCHAR(200) NOT NULL             COMMENT '商品名称',
    description   TEXT                              COMMENT '商品描述',
    brand         VARCHAR(100)                      COMMENT '品牌',
    status        TINYINT      DEFAULT 1            COMMENT '状态：0=下架，1=上架',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      DEFAULT 0            COMMENT '逻辑删除',
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SPU 标准产品单元表（商品基本信息）';

SELECT 'product_spu 表创建完成（Phase 2 Step 3）' AS message;

-- ============================================================
-- Phase 2 Step 4: SKU（库存量单位）表
-- ============================================================
DROP TABLE IF EXISTS product_sku;
CREATE TABLE product_sku (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'SKU ID',
    spu_id        BIGINT       NOT NULL             COMMENT '关联的 SPU ID',
    sku_code      VARCHAR(100) NOT NULL UNIQUE      COMMENT 'SKU 编码（唯一）',
    price         DECIMAL(10,2) NOT NULL            COMMENT '售价',
    stock         INT          NOT NULL DEFAULT 0   COMMENT '库存数量',
    specs         JSON                              COMMENT '规格属性（JSON格式，如 {"颜色":"黑色","内存":"128G"}）',
    status        TINYINT      DEFAULT 1            COMMENT '状态：0=禁用，1=启用',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      DEFAULT 0            COMMENT '逻辑删除',
    INDEX idx_spu_id (spu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU 库存量单位表';

-- 补充：为 SKU 表增加乐观锁 version 字段（Phase 2 Step 4 完善）
ALTER TABLE product_sku ADD COLUMN version INT DEFAULT 0 COMMENT '乐观锁版本号' AFTER stock;

SELECT 'product_sku 表创建完成（Phase 2 Step 4）' AS message;
