# demo018 - JWT + Spring Security 基础认证

**教学目标**：掌握 Spring Security + JWT 的现代无状态认证方式，实现登录签发 Token + 后续请求校验。

## 主要变化

- 引入 `spring-boot-starter-security` + `java-jwt`
- 新增 `JwtUtil`、`JwtAuthenticationFilter`、`SecurityConfig`
- 基于 `sys_user` 表实现真实用户登录
- 提供 `/auth/login` 接口签发 JWT
- 所有其他接口通过 `JwtAuthenticationFilter` 自动校验 Token
- 支持角色（ADMIN/TEACHER/STUDENT）

## 核心知识点

- Spring Security 6（函数式配置） + JWT 无状态认证
- JWT 的生成、校验、携带用户信息
- 登录流程与 Token 传递规范（Authorization: Bearer xxx）
- 基于数据库的用户认证集成
- 后续可扩展的权限控制基础（@PreAuthorize）

## 运行方式

```bash
# 确保已启动 Docker（MySQL + Redis）
docker compose up -d

cd demo018
mvn spring-boot:run
```

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