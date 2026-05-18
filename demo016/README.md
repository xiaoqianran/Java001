# demo016 - 统一日志 + 请求响应日志（MDC + 拦截器）

**教学目标**：掌握生产级项目中必备的**统一日志规范**，通过 MDC 实现 traceId 追踪，并记录完整的请求/响应日志。

## 主要变化

- 新增 `logback-spring.xml`，定义带颜色的、美观的日志格式
- 引入 `MDC`（Mapped Diagnostic Context），在整个请求链路中传递 `traceId`
- 新增 `LoggingFilter`（核心），使用 `ContentCachingRequestWrapper` + `ContentCachingResponseWrapper` 实现：
  - 完整记录 **Request Body**（JSON）
  - 完整记录 **Response Body**
  - 请求方法、URI、IP、Headers（敏感头脱敏）、耗时、状态码
- 所有日志自动带上 `traceId`，方便问题排查
- 在 Controller 中演示如何配合 traceId 输出业务日志
- 敏感字段（password、token）自动脱敏处理

## 核心知识点

- MDC 的原理和使用场景（线程上下文传递）
- `OncePerRequestFilter` + `ContentCachingRequestWrapper` / `ContentCachingResponseWrapper` 的正确用法
- 为什么必须在 finally 中调用 `responseWrapper.copyBodyToResponse()`
- 请求/响应体日志的性能与安全考虑（长度截断 + 敏感信息脱敏）
- logback 的 `%X{traceId}` 占位符用法
- 生产级统一日志的最佳实践（traceId + 完整请求链路）

## 运行方式

### 方式一：Docker 一键启动（推荐，demo016+）

```bash
# 在项目根目录执行（会同时启动 MySQL + Redis）
docker compose up -d

# 然后在 demo016 目录启动应用
cd demo016
mvn spring-boot:run
```

### 方式二：传统方式

确保本地 MySQL 已创建 `student_db` 并执行了 `sql/init.sql` 后：

```bash
cd demo016
mvn spring-boot:run
```

启动后调用接口（推荐用 POST 测试 body 记录），你会看到类似输出：

```
【请求开始】traceId=a1b2c3d4e5f6g7h8 | POST /students | IP=127.0.0.1 | Body={"name":"测试学生","age":21,"gender":1,"email":"test@school.com"}

【请求完成】traceId=a1b2c3d4e5f6g7h8 | 耗时=187ms | status=200 | ResponseBody={"success":true,"code":200,"message":"操作成功","data":15}
```

这样一次完整的请求和响应都被记录下来，排查问题极其方便。

**新数据库结构说明**（demo016 开始）：
- `sys_user`：账号体系（为 demo018 认证做准备）
- `t_class`：班级
- `t_student`：学生（已关联班级 + 学号 + 状态）

## 下一步

demo017 将集成 Redis 缓存（届时 `docker compose` 已包含 Redis 服务）。

---

**注意**：本 demo 仍是 Student 领域，用于聚焦**日志基础设施**的教学。真实业务项目重构将放在 demo020 之后。