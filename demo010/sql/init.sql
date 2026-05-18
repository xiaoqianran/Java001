-- ============================================================
-- 学生管理系统 - 统一数据库初始化脚本
-- 适用于 demo001 ~ demo00x 所有版本
-- ============================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS student_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci
    COMMENT '学生信息管理系统数据库';

-- 使用该数据库
USE student_db;

-- 删除旧表（方便反复执行初始化）
DROP TABLE IF EXISTS t_student;

-- 创建学生信息表
CREATE TABLE t_student (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生主键ID',
    name          VARCHAR(50)  NOT NULL               COMMENT '学生姓名',
    age           TINYINT UNSIGNED                    COMMENT '年龄',
    gender        TINYINT                             COMMENT '性别：0=女，1=男，2=未知',
    email         VARCHAR(100)                        COMMENT '邮箱地址',
    phone         VARCHAR(20)                         COMMENT '手机号码',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP  COMMENT '记录创建时间',
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生基本信息表';

-- 插入测试数据（方便你直接测试 CRUD）
INSERT INTO t_student (name, age, gender, email, phone) VALUES
('张三', 20, 1, 'zhangsan@example.com', '13800138001'),
('李四', 19, 0, 'lisi@example.com',   '13800138002'),
('王五', 21, 1, 'wangwu@example.com',  '13800138003');

-- 查看是否插入成功
SELECT * FROM t_student;
