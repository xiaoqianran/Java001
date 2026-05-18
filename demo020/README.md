# demo020 - 完整项目重构（模块划分 + 代码规范）

**教学目标**：对 demo001~demo019 积累的代码进行一次系统性重构，学习真实企业级项目的目录结构、DTO/VO 分离、模块化设计等最佳实践。

## 本次重构主要内容

- **DTO / VO 分离**：Controller 不再直接返回 Entity，引入 `StudentVO`
- **模块化目录**：按 `common` + `module` 划分
- **代码规范**：统一命名、统一转换逻辑、清晰的分层
- **可维护性提升**：为后续继续扩展（商品、订单等）打好基础

## 新的目录结构（简化版）

```
com.demo020.student
├── common
│   ├── config
│   ├── exception
│   ├── log
│   ├── result
│   └── security
├── controller
│   ├── dto          （输入）
│   └── vo           （输出）
├── entity
├── mapper
├── module
│   ├── auth
│   └── student
└── service
```

## 运行方式

仍然支持本地和 Docker 两种方式（继承 demo019）：

```bash
# 本地
mvn spring-boot:run

# Docker
docker compose up -d --build
```

## 下一步

本系列重构完成后，可基于此结构开始真正的「小型电商系统」全栈开发（Vite + Vue3 + 后端模块化）。

启动后访问：
- 应用：http://localhost:8080
- 登录接口：`POST /auth/login`

## 常用命令

```bash
# 停止并删除容器（保留数据卷）
docker compose down

# 彻底清理（包括数据库数据）
docker compose down -v

# 只重建应用（代码修改后）
docker compose up -d --build app

# 进入应用容器
docker exec -it student-app sh
```

## 环境变量覆盖说明

docker-compose.yml 中通过 `environment` 覆盖了 Spring 配置，让应用在容器内连接 `mysql` 和 `redis` 服务名（Docker 内部 DNS）。

本地开发仍然可以使用 `localhost`。

## 下一步

demo020 将进行完整项目重构（模块划分、代码规范、统一异常、DTO 拆分等）。

## 登录与认证测试

### 1. 获取 Token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 2. 使用 Token 访问接口
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/students
```

不带 Token 访问会返回 403 Forbidden。

## 下一步

demo019 将使用 Docker Compose 实现一键部署。

---

**注意**：本阶段仍使用 Student 领域，聚焦基础设施能力建设。完整真实项目将在 demo020 之后启动。