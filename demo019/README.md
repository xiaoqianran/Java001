# demo019 - Docker + Docker Compose 一键部署

**教学目标**：掌握 Spring Boot 应用的容器化与编排，实现「应用 + MySQL + Redis」一键部署。

## 主要新增文件

- `Dockerfile`：多阶段构建（构建 + 运行分离，镜像更小更安全）
- `docker-compose.yml`：编排三个服务（app、mysql、redis）
- `.dockerignore`：优化构建上下文

## 一键部署（推荐方式）

```bash
cd demo019

# 构建镜像并启动所有服务
docker compose up -d --build

# 查看日志
docker compose logs -f app

# 查看服务状态
docker compose ps
```

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