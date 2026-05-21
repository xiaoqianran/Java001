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

-- ============================================================
-- Phase 3 Step 1: 购物车表
-- ============================================================
DROP TABLE IF EXISTS cart;
CREATE TABLE cart (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    user_id       BIGINT NOT NULL                COMMENT '用户ID',
    sku_id        BIGINT NOT NULL                COMMENT 'SKU ID',
    quantity      INT NOT NULL DEFAULT 1         COMMENT '商品数量',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_sku (user_id, sku_id)     COMMENT '同一个用户+SKU只能有一条记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

SELECT 'cart 表创建完成（Phase 3 Step 1）' AS message;

-- ============================================================
-- Phase 4: 订单系统（demo022_mall 实现，demo023_mall 继承使用）
-- ============================================================

-- 订单主表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no      VARCHAR(50)  NOT NULL UNIQUE COMMENT '订单编号',
    user_id       BIGINT       NOT NULL COMMENT '下单用户ID',
    total_amount  DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    status        TINYINT      NOT NULL DEFAULT 10 COMMENT '订单状态：10=待支付, 20=已支付, 30=已发货, 40=已完成, 50=已取消, 60=已退款',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      DEFAULT 0,
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表（Phase 4）';

-- 订单明细表
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT       NOT NULL COMMENT '订单ID',
    sku_id        BIGINT       NOT NULL COMMENT 'SKU ID',
    sku_name      VARCHAR(200) COMMENT '商品名称快照',
    sku_specs     JSON         COMMENT '规格快照',
    price         DECIMAL(10,2) NOT NULL COMMENT '下单时单价',
    quantity      INT          NOT NULL COMMENT '购买数量',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表（Phase 4）';

SELECT 'order & order_item 表创建完成（Phase 4 - demo022_mall 实现，demo023_mall 继承）' AS message;

-- Phase 9: 支付单表（模拟第三方支付）
DROP TABLE IF EXISTS payment_order;
CREATE TABLE payment_order (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_no    VARCHAR(64)  NOT NULL UNIQUE COMMENT '支付单号',
    order_id      BIGINT       NOT NULL COMMENT '订单ID',
    order_no      VARCHAR(50)  NOT NULL COMMENT '订单号快照',
    user_id       BIGINT       NOT NULL COMMENT '用户ID',
    amount        DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    channel       VARCHAR(20)  DEFAULT 'MOCK' COMMENT '支付渠道',
    status        TINYINT      NOT NULL DEFAULT 10 COMMENT '10=待支付,20=支付成功,30=支付失败,40=已退款',
    callback_time DATETIME     COMMENT '回调时间',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      DEFAULT 0,
    UNIQUE KEY uk_payment_no (payment_no),
    UNIQUE KEY uk_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付单表（Phase 9）';

SELECT 'payment_order 表创建完成（Phase 9 - demo027_mall，Phase 10 新增已退款状态）' AS message;

-- ============================================================
-- Phase 11: 退款申请与审核（refund_order 表，demo029_mall 新增）
-- ============================================================
-- 设计要点：
-- - 独立于 payment_order，记录“申请事实 + 审核结果”
-- - status: 10=待审核, 20=已通过(已执行退款), 30=已拒绝
-- - Phase 11 简化为一个订单只能有一条退款申请记录（UNIQUE KEY uk_order_id），被拒绝后也不再重复申请
-- - 审核通过后才调用退款执行逻辑（订单/支付单状态变更 + 库存恢复）
-- ============================================================

DROP TABLE IF EXISTS refund_order;
CREATE TABLE refund_order (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '退款申请ID',
    order_id      BIGINT       NOT NULL COMMENT '关联的订单ID',
    user_id       BIGINT       NOT NULL COMMENT '申请人用户ID（BUYER）',
    reason        VARCHAR(255) NOT NULL COMMENT '退款原因（买家填写）',
    status        TINYINT      NOT NULL DEFAULT 10 COMMENT '10=待审核, 20=已通过, 30=已拒绝',
    apply_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    review_time   DATETIME     NULL COMMENT '审核时间',
    reviewer_id   BIGINT       NULL COMMENT '审核人用户ID（ADMIN/SELLER）',
    review_remark VARCHAR(255) NULL COMMENT '审核备注（通过说明或拒绝原因）',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_order_id (order_id),
    UNIQUE KEY uk_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请单（Phase 11，一订单一申请记录）';

SELECT 'refund_order 表创建完成（Phase 11 - demo029_mall 退款申请与审核流程）' AS message;
