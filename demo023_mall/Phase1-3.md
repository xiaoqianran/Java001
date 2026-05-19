# demo021_mall Phase 1-3 详细演进记录

> 本文档记录了 demo021_mall 从「demo020 成熟骨架」到当前状态所经历的三个主要阶段。
> 每个 Phase 都严格遵循「只做一个重点变化」的原则，目标是让你每次都能清楚回答：「这一步到底改了什么？为什么这么改？」

---

## 概述

### 为什么要做 Phase 划分？

- demo001~demo020 的目标是**打基础**（理解分层、MyBatis、Spring Boot、Docker、JWT 等）。
- demo021_mall 开始要做**真实业务**，业务复杂度显著上升，必须用「Phase + Step」的方式把变化拆小。
- 每个 Phase 对应一个**有业务价值的最小闭环**，方便后续版本（demo022_mall 等）做清晰的 diff 对比。

### demo021_mall 当前定位

- **已完成**：Phase 1（认证）、Phase 2（商品）、Phase 3（购物车）
- **核心价值**：拥有了一个可以真实「注册-登录-浏览商品-加购物车」的小型电商后端骨架。
- **下一步**：demo022_mall 将在此基础上添加**订单系统**（最有教学价值的交易闭环）。

---

## Phase 1：用户与认证体系（User + Auth + JWT + Security）

### 教学目标

把「能跑通」的 JWT 示例，升级为**生产级、可落地的认证授权体系**，并建立清晰的领域边界。

### 演进步骤（实际落地时拆得更细）

| Step | 主要改动 | 教学价值 |
|------|----------|----------|
| Step 1 | 建立 `sys_user` 表 + User 实体 + 注册接口 | 真实电商用户模型（角色、状态、逻辑删除） |
| Step 2 | 引入 BCrypt 密码加密 + 注册重复性校验 | 安全第一课 |
| Step 3 | 实现 JWT 生成与验证（JwtUtil） | 无状态认证核心 |
| Step 4 | 实现 JwtAuthenticationFilter + Spring Security 配置 | 把 JWT「真正用起来」 |
| Step 5 | 重构为 Auth 独立模块 + SecurityUtils + LoginUser + @PreAuthorize | 领域划分 + 方法级权限 |

### 关键代码位置与亮点

- **认证领域独立**：`module/auth/`（AuthController、AuthService、LoginVO）
  - 注册从原来的 `/api/user/register` 迁移到 `/api/auth/register`，体现「认证」是一个独立关注点。
- **用户信息传递**：
  - Filter 中把 `LoginUser` 放入 `SecurityContext`（principal）
  - 同时放到 request attribute（向后兼容 Controller 快速取用）
  - `SecurityUtils.getCurrentUser(request)` 提供统一获取方式
- **权限控制**：`@PreAuthorize("hasRole('ADMIN')")` 在 `AuthController.adminOnly()` 演示
- **UserDetailsServiceConfig**：提供空实现，避免 Spring Security 默认密码警告（因为我们完全走 JWT）

### 当前接口清单（Phase 1）

```
POST /api/auth/register
POST /api/auth/login          → 返回 {token, userId, username, nickname, role}
GET  /api/auth/me             → 返回当前 LoginUser 完整信息
GET  /api/auth/admin-only     → 只有 ADMIN 角色可访问
GET  /api/user/list           → 调试接口
GET  /api/debug/user/*        → 早期调试接口（可后续清理）
```

### 收获与设计决策

- 认证必须作为独立模块（Auth），而不是混在 User 里。
- `LoginUser` 是领域对象，不是简单的 `User` 复制。
- 角色前缀必须加 `ROLE_`，这是 Spring Security 硬性要求。
- Filter 永远不抛异常，静默放行（让 Security 决定 401/403）。

### 待改进点

- 目前 `/api/user/register` 仍被 permitAll（历史遗留，应清理）
- `SecurityUtils` 的无参 `getCurrentUser()` 实现仍不够完整（依赖 request 版本）

---

## Phase 2：商品领域模型（Category + SPU + SKU）

### 为什么按 Category → SPU → SKU 顺序推进？

这是电商领域最经典的「三级商品模型」：

1. **Category（分类）**：树形结构，影响展示和搜索
2. **SPU（Standard Product Unit）**：商品的「标准信息」（名称、描述、品牌），不包含规格和库存
3. **SKU（Stock Keeping Unit）**：真正参与交易的最小单位（价格、库存、具体规格）

这种拆分是后续「购物车」「订单」「库存扣减」的**数据基础**。

### 各 Step 核心改进

#### Phase 2 Step 1-2：商品分类（Category）

- 支持无限层级树形结构（`parent_id` + 递归/循环构建）
- `CategoryVO` 带 `children` 字段，直接给前端使用
- **安全删除**：检查是否有子分类，禁止误删
- 支持「移动分类」（修改 parent_id）
- 教学重点：**Controller 尽量薄，复杂业务规则下沉到 Service**

#### Phase 2 Step 3：SPU

- 基本 CRUD + 按分类查询（只查已上架）
- 独立的状态字段（上架/下架影响可见性）
- 为 SKU 做准备

#### Phase 2 Step 4：SKU + 乐观锁

- `specs` 字段使用 **JSON** 存储（`{"颜色":"黑色","内存":"128G"}`）
- **核心亮点**：引入 `@Version` 乐观锁实现 `reduceStock()`
  - 教学演示了「高并发下简单 `stock = stock - n` 会超卖」
  - MyBatis-Plus 会自动在 WHERE 条件里带上 version
  - 冲突时抛出「请重试」，上层可做重试或提示用户
- 额外提供了 `restoreStock`（为未来订单取消/支付失败准备）

### 当前接口清单（Phase 2）

```
GET    /api/category/tree
POST   /api/category
PUT    /api/category
PUT    /api/category/{id}/move
DELETE /api/category/{id}

GET    /api/spu/category/{categoryId}
POST   /api/spu
PUT    /api/spu
PUT    /api/spu/{id}/status
DELETE /api/spu/{id}

GET    /api/sku/spu/{spuId}
POST   /api/sku
POST   /api/sku/{id}/reduce     （测试扣库存接口）
DELETE /api/sku/{id}
```

### 收获

- 理解了「一个商品在不同维度下的不同抽象」（SPU vs SKU）
- 第一次在真实场景中使用乐观锁
- JSON 字段在 MyBatis-Plus 中的使用方式

### 待改进

- Category create 逻辑还写在 Controller（应移到 Service）
- safeDelete 缺少「分类下是否有商品」的校验（已有 TODO）
- SKU 列表接口缺少分页（后续大列表必须加）

---

## Phase 3：购物车（Cart）

### 为什么把购物车放在 Phase 3？

购物车是用户从「看」到「买」的**第一个有状态交互**。
它必须：
- 关联实时 SKU 信息（价格可能变、库存可能不足）
- 体现「同一个用户 + 同一个 SKU 只能有一条记录」（业务唯一性）

### 核心实现要点

- `addToCart`：存在则 `quantity += n`，不存在则新增（使用 MyBatis-Plus LambdaQuery）
- `getUserCart`：先查 Cart，再批量查 SKU，组装 `CartVO`（包含价格、库存、规格）
- 所有操作都基于 `userId + skuId` 唯一键
- 购物车接口全部通过 JWT 获取当前用户（`request.getAttribute("currentUserId")`）

### 接口清单

```
GET    /api/cart
POST   /api/cart          （添加，自动累加）
PUT    /api/cart          （修改数量）
DELETE /api/cart/{skuId}
```

### 教学价值

- 展示了**关联查询**的常用写法（先查主表再批量查关联表）
- 业务规则（「商品必须有效才能加购」）封装在 Service
- 为后续「下单时校验库存、生成订单清空购物车」埋下伏笔

### 待改进（已有记录）

- CartVO 缺少 SPU 的商品名称（TODO）
- CartController 仍使用老的 request attribute 方式，推荐统一改用 `SecurityUtils`

---

## 整体架构亮点（demo021_mall 已经具备）

1. **清晰的领域划分**：auth / user / product/category/spu/sku / cart
2. **双轨用户信息传递**：SecurityContext（推荐给 Service） + Request Attribute（Controller 方便）
3. **生产级可观测性**：全链路 traceId + 请求响应体记录
4. **防御性编程**：乐观锁、重复性校验、安全删除、状态校验
5. **演进友好**：所有表都有 `deleted`、`create_time`、`update_time`，为未来软删除、审计做准备

---

## demo021_mall 收尾修复（已完成）

### 2026-05-19 重要修复：Mapper 扫描与 MyBatis-Plus 配置问题

**问题现象**：启动后调用购物车等接口时出现：
```
No qualifying bean of type 'com.mall.module.product.sku.mapper.SkuMapper' available
```

**根本原因**（非业务代码问题）：
1. `application.yml` 中 `mybatis-plus` 配置错误地缩进到了 `spring:` 下面，导致 MyBatis-Plus 配置未生效。
2. `@MapperScan` 模式不够健壮（`com.mall.module.*.mapper` 无法可靠扫描深层 package）。

**修复内容**（严格按用户要求，未改任何业务逻辑）：
- `MallApplication.java`：改为 `@MapperScan("com.mall.**.mapper")`
- `application.yml`：将 `mybatis-plus:` 块移到 YAML 根级
- 验证了全部 5 个 Mapper（UserMapper、CategoryMapper、SpuMapper、SkuMapper、CartMapper）均正确实现了 `@Mapper + BaseMapper`

**验证结果**：
- 应用正常启动（Tomcat 8080）
- 所有接口（注册、登录、分类、SPU、SKU、购物车、权限控制等）通过 curl 实际测试全部返回成功
- 新增了 `API-接口文档.md`

---

在进入 demo022_mall 之前，我们对 demo021_mall 进行了以下**高质量收尾修复**（均符合「小步、有明确教学价值」的原则）：

1. **SecurityConfig 清理**：移除了已迁移的 `/api/user/register`（现在注册只在 `/api/auth/register`），并同步更新了 permit 列表和注释。
2. **CartController 统一**：所有方法改用 `SecurityUtils.getCurrentUser(request)` 获取当前用户，新增私有辅助方法，彻底消除直接操作 `request.getAttribute` 的老写法。
3. **Category 创建逻辑下沉**：`createCategory` 方法从 Controller 迁移到 Service，Controller 真正变「薄」，并在接口和实现中做了清晰注释。
4. **大量教学注释规范化**：核心模块的 `【mall - Phase X】` 标题统一更新为 `【demo021_mall - Phase X】`。
5. **小质量修复**：LoginVO 的 `@Builder` + 默认值警告已通过 `@Builder.Default` 消除。

这些改动全部通过编译验证，可直接作为「如何进行收尾重构」的教学案例。

---

## 已知不足与技术债务（给 demo022+ 的改进方向）（更新后）

| 问题 | 严重程度 | 建议在哪个版本解决 |
|------|----------|--------------------|
| Category create 映射写在 Controller | 低 | demo021 收尾或 demo022 |
| 旧的 `/api/user/register` 仍 permitAll | 中 | 立即清理 |
| CartController 未统一使用 SecurityUtils | 低 | demo022 |
| Redis 完全未使用 | 中 | demo024（缓存专题） |
| 缺少分页、排序、条件查询通用封装 | 中 | demo023 或之后 |
| 缺少统一的分页 VO（PageResult） | 低 | 尽快 |
| 调试接口（UserDebugController）未加权限 | 低 | demo022 |
| 缺少「商品详情」聚合接口（SPU+SKU 一起返回） | 中 | demo022 |

---

## 如何基于本版本继续演进（给未来 demo 的指导）

1. **永远先复制整个 demo021_mall 文件夹**，重命名为 demo022_mall
2. 在新文件夹里**只改一个重点**（例如「新增 order 模块 + 下单事务」）
3. 同步更新：
   - `pom.xml` 的 name/description
   - `MallApplication` 启动日志
   - 新增 `Phase4.md` 或在总文档里追加章节
   - `sql/init.sql` 里继续追加新表（用 Phase 注释）
4. 每次演进都要回答：
   - 这一步解决了什么真实痛点？
   - 对上一个版本的用户来说，升级成本是什么？
   - 代码 diff 里最核心的 20 行是什么？

---

**文档结束**

demo021_mall 的 Phase 1-3 到此告一段落。
我们已经拥有了一个**可以真实跑通业务闭环**的电商后端基础。

下一步最推荐的升级方向是 **「订单 + 事务扣库存」**，这将带来真正的「交易」教学价值。

需要我现在就开始准备 demo022_mall 的订单模块设计与实现吗？或者先把 demo021_mall 的小问题清理干净？