# demo021_mall 总结文档

> **创建日期**：2026-05-19  
> **状态**：Phase 1-3 已完成，已合并入主分支  
> **目的**：为后续 demo022_mall、demo023_mall 等版本演进提供清晰的回顾材料，便于查阅、对比和学习。

---

## 1. 项目定位

`demo021_mall` 是整个 **Java 教学系列** 的重要转折点：

- **demo001 ~ demo020**：以「学生管理系统」为教学载体，系统性学习从 JDBC 到 Spring Boot 生产级基础设施的全过程。
- **demo021_mall 开始**：正式转向**真实业务价值**的小型电商系统。

后续所有版本（demo022_mall ~ demo030+）都将围绕以下主线**循序渐进**演进：

**用户 → 商品 → 购物车 → 订单 → 支付 → 优化 → 工程化**

**demo021_mall 的核心价值**：
- 提供了第一个可真实运行的「注册-登录-浏览商品-加购物车」电商后端骨架。
- 建立了清晰的领域划分和生产级实践标准。
- 为后续交易类功能（订单、库存一致性）奠定坚实基础。

---

## 2. 演进背景

本项目严格遵循「**每一步只做一个重点变化**」的教学原则。

- 从 `demo020` 的成熟骨架（Result、异常处理、LoggingFilter + traceId、MyBatis-Plus、Docker、JWT 基础）直接演进。
- 不再使用「学生」示例，而是直接构建有实际业务意义的电商系统。
- 每个版本对应一个可独立运行、可 diff 对比的里程碑。

---

## 3. 已完成功能总览（Phase 1-3）

| Phase | 领域         | 核心交付                                                                 | 教学重点                              |
|-------|--------------|--------------------------------------------------------------------------|---------------------------------------|
| **1** | 用户与认证   | 注册（BCrypt + 重复校验）、JWT 登录、`/me`、角色体系（ADMIN/SELLER/BUYER）、`@PreAuthorize` 方法级权限 | JWT 无状态认证、Spring Security 集成、RBAC |
| **2** | 商品域       | Category（树形查询、移动、安全删除）、SPU、SKU（规格 + 价格 + 库存 + 乐观锁） | 三级商品模型、乐观锁防超卖             |
| **3** | 购物车       | 添加（自动累加）、修改数量、删除、列表（实时关联 SKU 信息）              | 关联查询、业务规则下沉、用户态购物车   |

### 已具备的生产级能力

- `Result<T>` 统一返回 + `BusinessException` + 全局异常处理
- `LoggingFilter` + MDC 全链路 traceId（请求/响应体记录）
- MyBatis-Plus + 逻辑删除 + 乐观锁（@Version）
- Spring Security + JWT 完整认证授权链路
- Redis 配置就绪（业务层尚未使用）
- Docker Compose（MySQL 3307 + Redis 6380）
- Actuator 健康检查

---

## 4. 架构与设计亮点

- **领域划分清晰**：`auth`、`user`、`product/category`、`product/spu`、`product/sku`、`cart`
- **用户信息传递双轨制**：
  - `SecurityContextHolder`（推荐 Service 层使用 `SecurityUtils.getCurrentUser()`）
  - Request Attribute（Controller 层快速获取）
- **防御性设计**：乐观锁、重复校验、安全删除、状态校验
- **教学友好**：核心类均有大段中文注释，说明「这一步改了什么、为什么」

---

## 5. 目录结构（demo021_mall）

```
demo021_mall/
├── README.md                    # 本版本快速入门
├── Phase1-3.md                  # 详细演进记录（强烈推荐阅读）
├── API-接口文档.md              # 完整接口清单 + 示例（已验证）
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── sql/init.sql
└── src/main/java/com/mall/
    ├── common/                  # Result、异常、日志、Security 公共能力
    ├── config/                  # Security、Redis、WebMvc 等配置
    ├── module/
    │   ├── auth/                # 认证领域（推荐独立）
    │   ├── user/
    │   ├── product/
    │   │   ├── category/
    │   │   ├── spu/
    │   │   └── sku/
    │   └── cart/
    └── MallApplication.java
```

---

## 6. 关键技术实现记录

### Phase 1 关键点
- Auth 模块独立（注册从 `/api/user/register` 迁移至 `/api/auth/register`）
- `LoginUser` 作为领域对象放入 SecurityContext
- `@PreAuthorize("hasRole('ADMIN')")` 演示方法级权限

### Phase 2 关键点
- Category 支持无限层级树 + 安全删除校验
- SKU 使用 JSON 字段存储规格 + `@Version` 实现乐观锁扣库存

### Phase 3 关键点
- 购物车添加时自动判断存在则累加
- Service 层批量查询 SKU 并组装 VO

### 重要收尾修复（2026-05-19）
- 修复 `mybatis-plus` 配置缩进错误（曾导致 SkuMapper Bean 缺失）
- 将 `@MapperScan` 优化为 `"com.mall.**.mapper"`
- 统一 CartController 使用 `SecurityUtils`
- Category 创建逻辑下沉到 Service
- 消除 LoginVO @Builder 警告

---

## 7. 接口概览

完整接口请查看 [demo021_mall/API-接口文档.md](demo021_mall/API-接口文档.md)

**核心公开接口**：
- `POST /api/auth/register`
- `POST /api/auth/login`

**需认证接口**（示例）：
- `GET /api/auth/me`
- `GET /api/category/tree`
- `POST /api/cart`
- `POST /api/sku/{id}/reduce`（乐观锁演示）

**初始管理员账号**：`admin / 123456`

---

## 8. 技术债务与待改进（供 demo022+ 参考）

- Redis 尚未在业务中使用（计划 demo024）
- 缺少统一分页封装
- 商品详情聚合接口缺失
- 调试接口未加权限保护
- 部分 Controller 仍可进一步瘦身
- `/api/health` 目前需要登录（可放开）

---

## 9. 下一步演进方向

**demo022_mall 推荐主题**：**订单系统（交易闭环）**

这是目前最有教学价值和业务价值的下一步：
- 订单创建 + 订单项
- 下单事务（购物车清空 + SKU 库存扣减）
- 乐观锁在真实交易场景的应用
- 订单状态流转基础

后续可能方向：
- demo023_mall：订单状态机 + 简单支付模拟
- demo024_mall：Redis 缓存 + 分布式锁
- ...

---

## 10. 学习建议

1. **优先阅读顺序**：
   - `demo021_mall/README.md`（快速了解）
   - `demo021_mall/Phase1-3.md`（最重要，理解每一步为什么这么做）
   - `demo021_mall/API-接口文档.md`（接口实践）
   - 本文档（全局视角）

2. **对比学习**：未来创建 demo022_mall 后，可直接对比两个文件夹的 diff，体会「小步演进」的价值。

3. **动手建议**：在理解当前代码基础上，尝试自己扩展一个小功能（如给 SKU 增加「销量」字段），再对比官方实现。

---

**文档结束**

`demo021_mall` 已完成从「教学骨架」到「可用电商基础」的转变。

准备好迎接下一个真正有挑战性的版本 —— **demo022_mall（订单系统）** 了吗？