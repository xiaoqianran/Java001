# Java + MySQL 循序渐进 CRUD 教学项目

> **目标**：把你从「完全不会写数据库操作」的小白，一步一步带到「能写出规范的分层 + Lombok 项目」。

## 本系列的设计理念（非常重要）

我们**严格按照「每一步只做一小件事」**的方式推进，绝不跳步。

目标是让你每次都能清楚回答：「这一步到底改了什么？为什么这么改？」

| 阶段 | 目录     | 核心特点                                                   | 教学重点（严格一步一步）                              |
|------|----------|------------------------------------------------------------|-------------------------------------------------------|
| 01   | demo001  | 最原始 JDBC（单文件）                                      | 看清 JDBC 底层到底在做什么                            |
| 02   | demo002  | 引入完整分层（无 Lombok，全手写 getter/setter）            | 理解分层价值 + 手动装配的麻烦                         |
| 03   | demo003  | **在 002 基础上只加 Lombok**（仅改 Entity）                | 第一次感受 Lombok 的威力（最小改动）                  |
| 04   | demo004  | **在 003 基础上只加日志**（@Slf4j）                        | 在已有 Lombok 的项目上，再增加日志功能                |
| 05   | demo005  | **MyBatis XML** 替换 Mapper 层（第一步）                   | 把手写 JDBC 替换成 MyBatis + XML                      |
| 06   | demo006  | **MyBatis 注解方式**（XML → @Select/@Insert）              | 对比两种 MyBatis SQL 写法                             |
| 07   | demo007  | **MyBatis-Spring 集成**                                    | 引入 Spring，彻底消灭手动 SqlSession 管理             |
| 08   | demo008  | **Spring Boot + MyBatis（最终版）**                        | 自动配置，现代项目标准写法                            |
| 09   | demo009  | **MyBatis-Plus BaseMapper**                                | Mapper 层极致简化                                     |
| 10   | demo010  | **MyBatis-Plus IService + ServiceImpl**                    | Service 层也大幅简化                                  |
| 11   | demo011  | **统一返回 Result + 全局异常处理**                         | 生产级项目必备的基础设施                              |
| 12   | demo012  | **RESTful API 改造**                                       | 从控制台程序转为现代 Web API                          |
| 13   | demo013  | **参数校验（Validation）**                                 | @Valid + 全局异常处理增强                             |
| 14   | demo014  | **分页 + 条件查询**                                        | MyBatis-Plus Page + 动态搜索                          |
| 15   | demo015  | **事务管理（@Transactional）**                             | 数据一致性保护 + 回滚演示                             |
| 04+  | 后续     | MyBatis / Spring Boot / 事务 / 分页 等     | 真实项目该怎么写                      |

**每一次升级都从上一个版本「复制粘贴」后进行改进**，你能清晰看到「**同样的功能，代码越来越少、越来越规范**」的过程。

---

## 1. 准备工作（必须先做）

### 1.1 一键启动 MySQL + Redis（推荐，demo016 开始）

我们提供了 `docker-compose.yml`，包含 MySQL 8 + Redis 7，并会自动执行初始化脚本。

```bash
# 启动数据库和缓存（后台运行）
docker compose up -d

# 查看状态
docker compose ps

# 查看日志
docker compose logs -f mysql redis
```

启动后：
- MySQL: `localhost:3306`（root / 123456 / student_db）
- Redis : `localhost:6379`（密码 123456）

**初始化脚本**已自动执行（包含 `sys_user`、`t_class`、`t_student` 更真实的表结构）。

### 1.2 传统手动方式（兼容旧习惯）

仍然支持：

```bash
docker run -d --name student-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=student_db \
  mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

# 手动导入
docker exec -i student-mysql mysql -uroot -p123456 < sql/init.sql
```

**注意**：从 demo016 开始，`sql/init.sql` 已经升级为更真实的「教务管理系统」模型（班级 + 学生 + 账号体系）。

### 1.3 验证连接

```bash
docker exec -it mysql-student mysql -uroot -p123456 -e "use student_db; show tables;"
```

---

## 2. 每个 Demo 的运行方式

每个 demo 都是**独立的 Maven 项目**，你可以：

```bash
cd demo001          # 或 demo002、demo003
mvn clean compile exec:java
```

或者在 IDEA 里直接打开对应 demo 文件夹作为独立项目运行。

---

## 3. 学习建议（给小白）

1. **不要跳着看**，一定要从 demo001 开始。
2. 每看完一个 demo，**先自己把代码手敲一遍**，不要复制。
3. 重点关注「**和上一个版本比，改了哪些地方？为什么这么改？**」
4. 每个类的顶部都有大量中文注释，**一定要认真读**。
5. 遇到不懂的 JDBC 概念（Connection、PreparedStatement、ResultSet），先在 demo001 里多敲几遍。

---

## 4. 后续规划（会持续更新）

- demo004：引入 MyBatis（去掉手写 JDBC）
- demo005：Spring Boot + MyBatis
- demo006：加入事务、统一返回结果、异常处理
- demo007：使用 Lombok + MapStruct + Validation 等现代写法

---

## 6. 真实业务演进系列：demo021_mall → demo030+（小型电商系统）

**重要转折**：从 demo021_mall 开始，我们彻底告别「学生管理系统示例」，转向**真正有业务价值的「小型电商系统」**。

### 设计理念（与前 20 个 demo 完全一致）

- 仍然严格「每一步只做一个重点变化」
- **每个版本都是在上一个版本的完整代码基础上复制 + 小步升级**
  - demo021_mall（Phase 1-3：用户认证 + 商品 + 购物车）
  - demo022_mall（新增订单 + 事务扣库存）
  - demo023_mall（订单状态机 + 支付模拟）
  - demo024_mall（Redis 缓存 + 分布式锁）
  - ... 一直演进到 demo030+（完整工程化、可生产部署的电商系统）

### 当前状态速览

| 版本           | 完成内容                        | 文档 |
|----------------|---------------------------------|------|
| demo021_mall   | Phase 1-3（用户+JWT+商品三级模型+购物车） | [README](./demo021_mall/README.md) + [Phase1-3.md](./demo021_mall/Phase1-3.md) |
| demo022_mall   | Phase 4（订单基础闭环 + 事务扣库存） | [README](./demo022_mall/README.md) |
| demo023_mall   | Phase 5（订单状态机 + 取消）     | [README](./demo023_mall/README.md) |
| ...            | ...                              | ... |
| demo029_mall   | Phase 11（退款申请与审核流程）   | [README](./demo029_mall/README.md) + [Phase11.md](./demo029_mall/Phase11.md) |
| demo030_mall   | Phase 12（部分退款与退款金额校验）**已完成** | [README](./demo030_mall/README.md) + [Phase12.md](./demo030_mall/Phase12.md) |

**强烈建议**：学习 demo021_mall 时，重点阅读 `Phase1-3.md`，它详细记录了每一阶段的教学决策。

---

## 5. 目录说明（更新后）

## 7. 目录说明（完整）

```
.
├── README.md
├── sql/                      # 学生系统初始化脚本（demo001~demo020 使用）
├── demo001/ ~ demo020/       # 学生管理系统教学系列（打基础）
├── demo021_mall/             # Phase 1-3（用户+商品+购物车）
├── demo022_mall/ ~ demo028_mall/
├── demo029_mall/             # Phase 11（退款申请与审核）
├── demo030_mall/             # Phase 12（部分退款金额校验）**已完成**
└── ... (持续演进)
```

---

## 8. 学习路线建议

**新手 / 想系统学 Java Web**：从 `demo001` 一直做到 `demo020`，再进入 `demo021_mall`。

**已有一定基础、想直接学真实项目**：可以从 `demo016` 或 `demo020` 快速过一遍基础设施，然后直接进入 `demo021_mall` 系列。

**想看「一步一步怎么从 0 做到一个可用电商系统」**：全程跟随 demo021_mall → ... → demo030_mall（Phase 12 部分退款金额校验已完成），每个版本的 PhaseX.md 都是最佳学习材料。

---

**开始你的学习之旅吧！**

- 打基础请从 [demo001/README.md](./demo001/README.md) 开始
- 想直接进入真实电商项目请从 [demo021_mall/README.md](./demo021_mall/README.md) 开始，并重点阅读 [Phase1-3.md](./demo021_mall/Phase1-3.md)
