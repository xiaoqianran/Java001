# Java 教学项目：从零到真实电商系统

> 一个严格遵循「每一步只做一个重点变化」原则的 Java 后端教学仓库

从最原始的 JDBC 开始，一步一步构建出**可真正运行、有业务价值的小型电商系统**。

---

## 🎯 项目目标

本仓库的目标不是教你“怎么用框架”，而是让你真正理解：

- 为什么需要分层？
- 为什么需要事务？
- 为什么需要乐观锁？
- 为什么真实项目要这样设计？

**每一次升级，你都能清晰看到“同样的功能，代码越来越少、越来越规范”。**

---

## 📚 两条并行的学习主线

本项目提供两条清晰的学习路径，满足不同基础的学习者：

### 1. 基础能力打磨系列（demo001 ~ demo020）

以「学生管理系统」为载体，**严格按照「每一步只做一个重点变化」**的原则，从最原始的 JDBC 一步步演进到现代 Spring Boot + MyBatis-Plus 的生产级开发方式。

这个系列的目标是帮你打牢后端开发的基础，让你真正理解每一层技术出现的必要性。

#### 版本演进索引（demo001 - demo020）

| Demo | 主题 | 核心教学目标 | 主要变化 | 详细说明 |
|------|------|--------------|----------|----------|
| demo001 | 最原始 JDBC | 看清 JDBC 底层原理 | 单文件硬编码 JDBC + Scanner 控制台 | [详细说明](./demo001/README.md) |
| demo002 | 传统分层架构 | 理解分层与手动装配的痛苦 | 拆分为 Entity/Mapper/Service/Controller，手写 getter/setter | [详细说明](./demo002/README.md) |
| demo003 | 引入 Lombok | 感受最小改动带来的巨大收益 | 仅 Entity 使用 @Data + @Builder | [详细说明](./demo003/README.md) |
| demo004 | 加入日志 | 在已有项目上优雅增加新能力 | 增加 @Slf4j 日志 | [详细说明](./demo004/README.md) |
| demo005 | MyBatis XML | 第一次接触 ORM | 用 MyBatis XML 完全替换手写 JDBC | [详细说明](./demo005/README.md) |
| demo006 | MyBatis 注解 | 对比两种 MyBatis 写法 | XML 迁移到 @Select/@Insert 注解 | [详细说明](./demo006/README.md) |
| demo007 | MyBatis-Spring | 进入依赖注入时代 | 引入 Spring 管理 SqlSession | [详细说明](./demo007/README.md) |
| demo008 | Spring Boot | 掌握现代项目标准写法 | 自动配置，抛弃 XML，使用 application.yml | [详细说明](./demo008/README.md) |
| demo009 | MyBatis-Plus BaseMapper | Mapper 层极致简化 | Mapper 继承 BaseMapper，无需手写 CRUD | [详细说明](./demo009/README.md) |
| demo010 | MyBatis-Plus Service | Service 层也大幅简化 | Service 继承 ServiceImpl | [详细说明](./demo010/README.md) |
| demo011 | 统一返回 + 全局异常 | 建立生产级响应规范 | Result<T> + GlobalExceptionHandler | [详细说明](./demo011/README.md) |
| demo012 | RESTful API 改造 | 从控制台程序转型 Web 项目 | 移除控制台菜单，改为 @RestController | [详细说明](./demo012/README.md) |
| demo013 | 参数校验（Validation） | 接口参数健壮性 | DTO + @Valid + 校验注解增强 | [详细说明](./demo013/README.md) |
| demo014 | 分页 + 条件查询 | 掌握真实项目常用列表功能 | MyBatis-Plus Page + LambdaQueryWrapper | [详细说明](./demo014/README.md) |
| demo015 | 事务管理 | 理解数据一致性保护 | @Transactional + 批量操作回滚演示 | [详细说明](./demo015/README.md) |
| demo016 | 统一日志 + 请求响应日志 | 生产级可观测性 | MDC + LoggingFilter + 完整请求/响应体记录 + 脱敏 | [详细说明](./demo016/README.md) |
| demo017 | Redis 缓存集成 | 正确使用缓存这把「双刃剑」 | @Cacheable + 缓存穿透/雪崩防护 | [详细说明](./demo017/README.md) |
| demo018 | JWT + Spring Security | 现代无状态认证体系 | JWT 登录 + 自定义过滤器 + 基于真实用户表认证 | [详细说明](./demo018/README.md) |
| demo019 | Docker + Docker Compose | 项目具备开箱即用部署能力 | 多阶段 Dockerfile + 完整 docker-compose 编排 | [详细说明](./demo019/README.md) |
| demo020 | 完整项目重构 | 从「能跑」进化到「好维护」 | DTO/VO 彻底分离 + 目录模块化 + 代码规范 | [详细说明](./demo020/README.md) |

**学习建议**：这个系列适合想系统打牢基础的同学。建议配合 Git 做版本对比学习。

---

### 2. 真实业务实战演进系列（demo021_mall ~ demo030_mall）⭐ 推荐主线

**从 demo021_mall 开始，我们彻底告别学生管理系统，转向构建一个真正有业务价值的小型电商系统。**

这个系列严格遵循「每一步只做一个重点变化」的原则，每一个版本都只解决电商领域中的一个核心问题，方便学习和前后对比。

#### 电商系统演进索引（demo021_mall ~ demo030_mall）

| Demo           | Phase | 核心交付                                              | 教学重点                                      | 详细文档 |
|----------------|-------|-------------------------------------------------------|-----------------------------------------------|----------|
| demo021_mall   | 1-3   | 用户认证(JWT+RBAC) + 商品域(分类/SPU/SKU) + 购物车    | 现代认证体系、三级商品模型、关联查询           | [README](./demo021_mall/README.md) + [Phase1-3.md](./demo021_mall/Phase1-3.md) |
| demo022_mall   | 4     | 订单创建 + 事务扣减库存 + 清空购物车                  | @Transactional + 数据一致性                   | [README](./demo022_mall/README.md) + [Phase4.md](./demo022_mall/Phase4.md) |
| demo023_mall   | 5     | 订单状态机 + 取消订单                                 | 状态机设计 + 原子条件更新                     | [README](./demo023_mall/README.md) + [Phase5.md](./demo023_mall/Phase5.md) |
| demo024_mall   | 6     | 简单支付模拟（10 → 20）                               | 状态流转 + 幂等性                             | [README](./demo024_mall/README.md) + [Phase6.md](./demo024_mall/Phase6.md) |
| demo025_mall   | 7     | 发货与完成（20 → 30 → 40）                            | 角色权限 + 状态机扩展                         | [README](./demo025_mall/README.md) + [Phase7.md](./demo025_mall/Phase7.md) |
| demo026_mall   | 8     | 订单超时自动取消                                      | @Scheduled + 并发安全                         | [README](./demo026_mall/README.md) + [Phase8.md](./demo026_mall/Phase8.md) |
| demo027_mall   | 9     | 模拟支付回调 + 幂等处理                               | 支付模型 + 回调事务边界 + 幂等设计            | [README](./demo027_mall/README.md) + [Phase9.md](./demo027_mall/Phase9.md) |
| demo028_mall   | 10    | 模拟退款流程（20 → 60）                               | 退款事务边界 + 条件更新并发安全               | [README](./demo028_mall/README.md) + [Phase10.md](./demo028_mall/Phase10.md) |
| demo029_mall   | 11    | 退款申请与审核流程                                    | 工作流 + 审批流 + 申请与执行分离              | [README](./demo029_mall/README.md) + [Phase11.md](./demo029_mall/Phase11.md) |
| **demo030_mall** | **12** | **部分退款与退款金额校验** ★ 当前最新               | **金额校验 + 财务字段设计**                   | [README](./demo030_mall/README.md) + [Phase12.md](./demo030_mall/Phase12.md) |

**强烈推荐**：学习 mall 系列时，务必同时阅读对应版本的 `PhaseX.md`，它记录了该阶段所有的设计决策和取舍。

---

## 📖 如何最高效地使用本仓库

1. **永远不要跳着看**，一定要按顺序。
2. **最高效的学习方法**：使用 Git 做版本对比  
   ```bash
   git diff demo029_mall demo030_mall
   ```
3. 每个版本的 `README.md` + 对应的 `PhaseX.md` 配合阅读，效果最佳。
4. 重点思考：「**为什么上一个版本不行？这一步解决了什么问题？**」

---

## 🚀 快速开始

### 方式一：直接进入电商实战（推荐）

```bash
cd demo030_mall          # 或 demo029_mall、demo021_mall 等任意版本
docker compose up -d     # 启动 MySQL + Redis
mvn clean spring-boot:run
```

访问：`http://localhost:8080`

### 方式二：系统学习基础（适合新手）

从 `demo001` 开始，依次完成到 `demo020`，再进入 `demo021_mall`。

---

## 🗂 项目目录结构

```
.
├── README.md
├── sql/                                    # 初始化脚本
├── demo001/ ~ demo020/                     # 基础能力打磨系列（学生管理系统）
├── demo021_mall/ ~ demo030_mall/           # 真实电商系统实战演进系列（当前最新）
└── ...
```

---

## 💡 设计哲学

本项目最核心的原则只有一条：

> **每一步只做一个重点变化**

我们拒绝大跃进式的教学。每一个版本、每一个 commit，都只解决一个具体问题，让你能清晰地感受到进步。

---

## 📌 后续规划

- demo031_mall：多次部分退款 + `refund_order_item` 明细表
- 真实支付渠道集成（微信/支付宝退款回调）
- 更多工程化实践（配置中心、链路追踪、单元测试等）

---

**开始你的学习之旅吧！**

- 想系统打基础 → 从 [demo001](./demo001) 开始
- 想直接学真实电商项目 → 从 [demo021_mall](./demo021_mall) 开始，并重点阅读 [Phase1-3.md](./demo021_mall/Phase1-3.md)

---

*本项目持续更新，欢迎 Star ⭐ 关注最新进展。*
