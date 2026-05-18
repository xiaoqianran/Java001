# Mall - 小型电商系统（教学演进版）

> **核心理念**：严格遵循「每一步只做一个重点变化」，从 demo020 的成熟骨架出发，一步一步构建真正有业务价值的电商系统。

## 当前阶段（v0 - 极简生产级骨架）

本阶段只完成一件事：

- 建立独立 `mall` 项目
- 继承 demo020 最佳实践：
  - `Result<T>` 统一返回
  - `BusinessException` + `GlobalExceptionHandler`
  - `LoggingFilter` + MDC 全链路 traceId（含请求/响应体记录 + 脱敏）
- 打通 MySQL + Redis 连接
- 集成 Actuator 健康检查
- Docker Compose 一键启动基础设施

**此时项目里没有任何业务功能**（没有用户、没有商品、没有订单）。

## 快速启动

### 方式一：本地开发（推荐）

```bash
# 1. 启动数据库和 Redis（只需一次）
cd mall
docker compose up -d

# 2. 编译运行（注意：此时 DB 用 3307，Redis 用 6380）
cd mall
mvn clean spring-boot:run
```

启动后访问：
- 健康检查（自定义）：http://localhost:8080/api/health
- Actuator 健康检查：http://localhost:8080/actuator/health

**注意**：mall 项目使用独立端口，避免与旧的 student 系统冲突：
- MySQL：3307
- Redis：6380

### 方式二：完全 Docker（后续阶段会完善）

```bash
docker compose up -d --build
```

## 目录结构（当前）

```
mall/
├── src/main/java/com/mall/
│   ├── common/                  # 跨领域公共能力（从 demo020 继承）
│   │   ├── result/Result.java
│   │   ├── exception/...
│   │   └── log/（LoggingFilter + TraceIdUtil）
│   ├── config/                  # 配置类
│   │   ├── WebMvcConfig.java    # 注册 LoggingFilter
│   │   └── RedisConfig.java
│   ├── controller/
│   │   └── HealthController.java
│   └── MallApplication.java
├── src/main/resources/
│   └── application.yml
├── sql/init.sql
├── docker-compose.yml
├── Dockerfile（待添加）
└── pom.xml
```

## 下一步计划

等这个骨架验证通过后，我们会**一起商量**下一个要加的功能（极大概率是「用户注册 + 登录 + JWT」）。

请直接在当前会话告诉我：
- 这个骨架是否符合你的预期？
- 有没有需要调整的地方（比如日志格式、包结构等）？
- 准备好进入下一个小步骤了吗？

---

**记住我们的原则**：不贪多，每一步都清晰可解释。
