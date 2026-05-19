# demo022_mall - 小型电商系统演进版（Phase 4 开发中：订单系统）

> **核心教学理念**：严格遵循「每一步只做一个重点变化」。
> 从 demo020 的成熟骨架出发，一步一步构建真正有业务价值的电商系统。
> 每个版本（demo021_mall、demo022_mall … demo030+）都是在上一个版本基础上**小步升级**。

> **当前状态**：Phase 4 开发中 —— 正在实现订单系统（事务 + 库存扣减 + 购物车联动）。

---

## 项目定位

`demo021_mall` 是整个教学系列的**重要转折点**：

- **demo001 ~ demo020**：以「学生管理系统」为载体，系统性地学习 JDBC → MyBatis → Spring Boot → 生产级基础设施（日志、异常、事务、Docker、JWT 等）。
- **demo021_mall 开始**：转向**真实业务**——小型电商系统。所有后续版本都围绕「用户 → 商品 → 购物车 → 订单 → 支付 → 优化 → 工程化」这条主线，**循序渐进**地深入。

**本版本（demo022_mall）** 在 demo021_mall 的基础上继续演进，当前正在开发 **Phase 4：订单系统**（下单事务、库存扣减、购物车清空）。

---

## 当前已实现功能总览

| Phase | 领域         | 关键功能                                                                 | 教学重点 |
|-------|--------------|--------------------------------------------------------------------------|----------|
| **1** | 用户与认证   | 注册（重复校验+BCrypt）、登录返回 JWT、`/me`、角色体系（ADMIN/SELLER/BUYER）、`@PreAuthorize` 方法级权限 | JWT 无状态认证、Spring Security 集成、RBAC |
| **2** | 商品域       | Category（树形查询、移动、安全删除）、SPU（商品基本信息）、SKU（规格、价格、库存 + 乐观锁） | 三级商品模型设计、乐观锁防超卖 |
| **3** | 购物车       | 添加（累加）、修改数量、删除、列表（实时关联 SKU 价格/库存/规格）        | 关联查询、业务规则封装、用户态购物车 |

**已具备的生产级能力**（全部继承自 demo020 并强化）：
- `Result<T>` 统一返回 + `BusinessException` + 全局异常处理
- `LoggingFilter` + MDC traceId 全链路日志（含请求/响应体）
- MyBatis-Plus + 逻辑删除 + 乐观锁
- Spring Security + JWT 完整认证授权链路
- Redis 配置就绪（尚未业务化使用）
- Docker Compose 一键启动（MySQL 3307 + Redis 6380）
- Actuator 健康检查

---

## 快速启动

### 方式一：本地开发（推荐）

```bash
# 1. 启动基础设施（MySQL + Redis）
cd demo021_mall
docker compose up -d

# 2. 运行应用
cd demo021_mall
mvn clean spring-boot:run
```

启动后访问：
- 自定义健康检查：http://localhost:8080/api/health
- Actuator：http://localhost:8080/actuator/health

**端口说明**（与学生系统隔离）：
- 应用：8080
- MySQL：3307
- Redis：6380

### 方式二：完整 Docker 构建运行

```bash
docker compose up -d --build
```

---

## 目录结构

```
demo021_mall/
├── src/main/java/com/mall/
│   ├── common/                  # 跨领域公共能力（Result、异常、日志、Security）
│   ├── config/                  # 配置类（Security、Redis、WebMvc 等）
│   ├── controller/              # 健康检查
│   ├── module/
│   │   ├── auth/                # 认证领域（register/login/me）
│   │   ├── user/                # 用户领域
│   │   ├── product/
│   │   │   ├── category/        # 商品分类（树形）
│   │   │   ├── spu/             # 标准产品单元
│   │   │   └── sku/             # 库存量单位 + 乐观锁库存
│   │   └── cart/                # 购物车
│   └── MallApplication.java
├── src/main/resources/
│   └── application.yml
├── sql/init.sql                 # 按 Phase 逐步演进的建表脚本
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## 阶段性文档（强烈建议阅读）

本系列非常重视**文档与演进记录**：

- **[Phase1-3.md](./Phase1-3.md)**（必读）  
  详细拆解 Phase 1~3 每个阶段的教学目标、关键变更点、收获、已知不足。
- **[API-接口文档.md](./API-接口文档.md)**  
  完整的后端接口清单 + 请求示例 + 认证说明（已实际测试通过）。

---

## 下一步演进计划

我们将以 **demo022_mall、demo023_mall …** 的方式继续演进，每个版本只做**一到两个有明确教学价值**的升级：

| 下一版本       | 计划新增内容                     | 核心教学点                     |
|----------------|----------------------------------|--------------------------------|
| demo022_mall   | 订单模块（下单事务 + 库存扣减）  | 分布式事务边界、乐观锁实战     |
| demo023_mall   | 订单状态机 + 取消/支付模拟       | 状态模式、领域事件             |
| demo024_mall   | Redis 缓存策略 + 分布式锁        | 缓存一致性、Redisson           |
| ...            | ...                              | ...                            |

**原则永远不变**：不贪多，每一步都清晰可解释、可回溯。

---

## 学习建议

1. 先完整阅读 `Phase1-3.md`，理解每个 Phase 的演进理由。
2. 每看完一个模块，尝试自己扩展一个小功能（例如给 SPU 增加「销量」字段）。
3. 未来当你想看「demo022_mall 是如何从 demo021_mall 升级而来」时，对比两个文件夹的 diff 即可。
4. 所有核心类都有大段中文教学注释，请认真阅读。

---

**记住我们的原则**：  
**每一步都只做一件事，而且这件事必须有清晰的教学价值。**

准备好进入下一个小步骤了吗？随时告诉我——我们是继续优化 demo021_mall，还是直接开始 demo022_mall 的订单系统？