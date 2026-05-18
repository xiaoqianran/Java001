-- ============================================================
-- 学生教务管理系统 - 增强版初始化脚本（demo016 开始使用）
-- ============================================================
-- 设计目标：更贴近真实学校系统，包含账号体系 + 班级 + 学生
-- 为后续 demo018（认证）、demo020（重构）做好数据模型铺垫
--
-- 注意：当使用 docker-compose + MYSQL_DATABASE 时，数据库已由 entrypoint 创建
-- 本脚本直接使用 student_db，禁止再次 CREATE DATABASE
-- ============================================================

USE student_db;

-- ============================================================
-- 1. sys_user 账号表（登录体系）
--    为 demo018 JWT + Spring Security 做准备
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username      VARCHAR(50)  NOT NULL UNIQUE      COMMENT '登录账号',
    password      VARCHAR(100) NOT NULL             COMMENT '密码（BCrypt 加密后）',
    real_name     VARCHAR(50)                       COMMENT '真实姓名',
    role          TINYINT      NOT NULL DEFAULT 2   COMMENT '角色：1=管理员，2=教师，3=学生',
    status        TINYINT      NOT NULL DEFAULT 1   COMMENT '状态：1=正常，0=禁用',
    student_id    BIGINT                            COMMENT '关联学生ID（角色为学生时有值）',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户账号表';

-- ============================================================
-- 2. t_class 班级表
-- ============================================================
DROP TABLE IF EXISTS t_class;
CREATE TABLE t_class (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    class_name    VARCHAR(50)  NOT NULL             COMMENT '班级名称（如：软件工程2022级1班）',
    grade         SMALLINT                          COMMENT '年级（如：2022）',
    teacher_name  VARCHAR(50)                       COMMENT '班主任姓名',
    student_count INT          DEFAULT 0            COMMENT '学生人数（可冗余或触发器维护）',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级信息表';

-- ============================================================
-- 3. t_student 学生表（核心业务表，已关联班级）
-- ============================================================
DROP TABLE IF EXISTS t_student;
CREATE TABLE t_student (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
    student_no    VARCHAR(20)  NOT NULL UNIQUE      COMMENT '学号（真实系统中非常重要）',
    name          VARCHAR(50)  NOT NULL               COMMENT '姓名',
    age           TINYINT UNSIGNED                    COMMENT '年龄',
    gender        TINYINT      DEFAULT 2              COMMENT '0=女，1=男，2=未知',
    class_id      BIGINT                              COMMENT '所属班级ID',
    status        TINYINT      DEFAULT 1              COMMENT '1=在读，2=毕业，3=休学，4=退学',
    phone         VARCHAR(20)                         COMMENT '手机号',
    email         VARCHAR(100)                        COMMENT '邮箱',
    avatar        VARCHAR(255)                        COMMENT '头像URL',
    birthday      DATE                                COMMENT '出生日期',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_class FOREIGN KEY (class_id) REFERENCES t_class(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生基本信息表';

-- ============================================================
-- 插入测试数据（更真实）
-- ============================================================

-- 班级
INSERT INTO t_class (id, class_name, grade, teacher_name) VALUES
(1, '软件工程2022级1班', 2022, '张老师'),
(2, '软件工程2022级2班', 2022, '李老师'),
(3, '计算机科学2023级1班', 2023, '王老师');

-- 学生（分布在不同班级）
INSERT INTO t_student (student_no, name, age, gender, class_id, status, phone, email) VALUES
('202201001', '张伟',   21, 1, 1, 1, '13800138001', 'zhangwei@school.edu.cn'),
('202201002', '李娜',   20, 0, 1, 1, '13800138002', 'lina@school.edu.cn'),
('202201015', '王强',   22, 1, 1, 1, '13800138003', 'wangqiang@school.edu.cn'),
('202202003', '陈静',   20, 0, 2, 1, '13800138004', 'chenjing@school.edu.cn'),
('202202007', '刘洋',   21, 1, 2, 2, '13800138005', 'liuyang@school.edu.cn'),  -- 已毕业
('202203001', '赵敏',   19, 0, 3, 1, '13800138006', 'zhaomin@school.edu.cn');

-- 系统账号（密码都是 123456 的 BCrypt 占位，真实项目会用 BCryptPasswordEncoder）
-- 注意：demo018 后会真正加密存储
INSERT INTO sys_user (username, password, real_name, role, status, student_id) VALUES
('admin',   '$2a$10$7JB80U2q5n5pN5v5v5v5vO5v5v5v5v5v5v5v5v5v5v5v5v5v5v5v5v5', '系统管理员', 1, 1, NULL),
('zhanglaoshi', '$2a$10$7JB80U2q5n5pN5v5v5v5vO5v5v5v5v5v5v5v5v5v5v5v5v5v5v5v5v5', '张老师', 2, 1, NULL),
('zhangwei', '$2a$10$7JB80U2q5n5pN5v5v5v5vO5v5v5v5v5v5v5v5v5v5v5v5v5v5v5v5v5', '张伟', 3, 1, 1);  -- 学生账号

-- 更新班级学生人数（简单模拟）
UPDATE t_class SET student_count = 3 WHERE id = 1;
UPDATE t_class SET student_count = 2 WHERE id = 2;
UPDATE t_class SET student_count = 1 WHERE id = 3;

-- 验证
SELECT '=== 班级 ===' AS info;
SELECT * FROM t_class;
SELECT '=== 学生（含班级） ===' AS info;
SELECT s.id, s.student_no, s.name, c.class_name, s.status FROM t_student s LEFT JOIN t_class c ON s.class_id = c.id;
SELECT '=== 用户账号 ===' AS info;
SELECT id, username, real_name, role, status FROM sys_user;
