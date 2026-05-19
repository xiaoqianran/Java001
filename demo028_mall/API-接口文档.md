# demo028_mall API 接口文档（Phase 10 - 模拟退款流程）

> **项目**：demo028_mall - 小型电商系统演进版（Phase 10：模拟退款流程）  
> **当前版本**：Phase 1-10 已完成（用户认证 + 商品域 + 购物车 + 订单基础 + 状态机/取消 + 支付模拟 + 发货完成 + 超时自动取消 + 支付回调幂等 + 模拟退款）  
> **基础地址**：`http://localhost:8080`  
> **认证方式**：JWT Bearer Token（Header: `Authorization: Bearer <token>`）

---

## 1. 认证与用户（Auth / User）

### 1.1 用户注册

`POST /api/auth/register`

**请求 Body**:
```json
{
  "username": "string（必填，唯一）",
  "password": "string（必填）",
  "nickname": "string（可选）",
  "phone": "string（可选，唯一）",
  "email": "string（可选）"
}
```

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "注册成功",
  "data": { "id": 2, "username": "...", "nickname": "...", "role": 3, ... }
}
```

---

### 1.2 用户登录

`POST /api/auth/login`

**请求 Body**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "admin",
    "nickname": "...",
    "role": 1
  }
}
```

> **初始管理员账号**：`admin / 123456`（来自 sql/init.sql）

---

### 1.3 获取当前登录用户信息

`GET /api/auth/me`

**Header**: `Authorization: Bearer <token>`

**响应**:
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "admin",
    "role": 1,
    "admin": true
  }
}
```

---

### 1.4 管理员专用接口（演示 @PreAuthorize）

`GET /api/auth/admin-only`

**需要角色**：ADMIN（role=1）

---

## 2. 商品分类（Category）

### 2.1 获取完整分类树

`GET /api/category/tree`

**响应**：返回带 `children` 的树形结构

---

### 2.2 创建分类

`POST /api/category`

**请求**:
```json
{
  "parentId": 0,
  "name": "手机数码",
  "level": 1,
  "sort": 1,
  "icon": "",
  "description": ""
}
```

---

### 2.3 更新分类

`PUT /api/category`

---

### 2.4 移动分类

`PUT /api/category/{id}/move?newParentId=xx`

---

### 2.5 安全删除分类

`DELETE /api/category/{id}`

> 有子分类时禁止删除

---

## 3. SPU（标准产品单元）

### 3.1 按分类查询 SPU 列表（已上架）

`GET /api/spu/category/{categoryId}`

---

### 3.2 创建 SPU

`POST /api/spu`

---

### 3.3 更新 SPU

`PUT /api/spu`

---

### 3.4 上架/下架

`PUT /api/spu/{id}/status?status=1`

---

### 3.5 删除 SPU（逻辑删除）

`DELETE /api/spu/{id}`

---

## 4. SKU（库存量单位）

### 4.1 按 SPU 查询 SKU 列表

`GET /api/sku/spu/{spuId}`

---

### 4.2 创建 SKU

`POST /api/sku`

**示例 specs**:
```json
{
  "spuId": 1,
  "skuCode": "IPHONE15-128-BLACK",
  "price": 5999.00,
  "stock": 100,
  "specs": { "颜色": "黑色", "内存": "128G" }
}
```

---

### 4.3 扣减库存（测试接口，带乐观锁）

`POST /api/sku/{id}/reduce?quantity=1`

---

### 4.4 删除 SKU

`DELETE /api/sku/{id}`

---

## 5. 购物车（Cart）

**所有接口均需要登录**

### 5.1 获取当前用户购物车

`GET /api/cart`

**响应**：关联实时 SKU 的价格、库存、规格

---

### 5.2 添加到购物车

`POST /api/cart`

```json
{
  "skuId": 1,
  "quantity": 2
}
```

> 已存在则自动累加数量

---

### 5.3 修改购物车数量

`PUT /api/cart`

```json
{
  "skuId": 1,
  "quantity": 5
}
```

---

### 5.4 删除购物车商品

`DELETE /api/cart/{skuId}`

---

## 6. 调试接口（仅开发环境使用）

- `GET /api/user/list`（仅 ADMIN 可访问，且不返回 password）
（调试接口已删除，普通用户无权访问）

> 建议在生产环境关闭或加上 `@PreAuthorize`

---

## 7. 健康检查与监控

- `GET /actuator/health` （推荐）
- `GET /api/health` （当前需登录，可后续放开）

---

## 8. 错误响应格式（统一）

所有接口失败时返回：

```json
{
  "success": false,
  "code": 400,
  "message": "错误信息"
}
```

---

## 9. 认证说明

- 除注册、登录、actuator 外，其余接口**必须携带有效 JWT**
- Token 有效期：24小时（可配置）
- 角色体系：1=ADMIN、2=SELLER、3=BUYER

---

## 10. 订单接口（Phase 4/5 新增）

### 创建订单
`POST /api/order`

会自动完成：
- 创建订单记录
- 扣减对应 SKU 库存（使用乐观锁）
- 清空购物车中已下单的商品

**请求示例**（同 Phase 4）

### 我的订单列表
`GET /api/order?page=1&size=10`

### 订单详情
`GET /api/order/{id}`

### 取消订单（Phase 5 新增）
`PUT /api/order/{id}/cancel`

**业务规则**：
- 仅 `status=10`（待支付）的订单可以取消
- 只能取消**自己的**订单
- 取消成功后：订单状态变为 50（已取消），对应 SKU 库存全部回滚
- 整个操作在同一个事务中，失败自动回滚
- 已支付、已发货、已完成、已取消的订单**均不可取消**

**请求**：无 Body，路径携带订单ID

**成功响应**（符合项目 Result 统一格式）：
```json
{
  "success": true,
  "code": 200,
  "message": "取消成功",
  "data": null
}
```

**失败场景示例**：
- 未登录：401 + "请先登录"
- 非本人订单：403 + "订单不存在或无权操作该订单"
- 状态不允许：业务错误 "只有待支付的订单可以取消，当前状态：20(已支付)"
- 重复取消：同上（终态保护）

**示例 curl**：
```bash
curl -X PUT http://localhost:8080/api/order/42/cancel \
  -H "Authorization: Bearer $TOKEN"
```

### 支付订单（Phase 6 新增）
`PUT /api/order/{id}/pay`

**业务规则**：
- 仅 `status=10`（待支付）的订单可以支付
- 只能支付**自己的**订单
- 支付成功后：订单状态变为 20（已支付）
- 已取消、已支付、已完成、已发货的订单**均不可支付**
- 使用原子条件更新防止并发重复支付

**成功响应**（符合项目 Result 统一格式）：
```json
{
  "success": true,
  "code": 200,
  "message": "支付成功",
  "data": null
}
```

**失败场景**：
- 未登录：401
- 非本人订单或状态不允许：业务错误 "订单状态已变化，不能支付"
- 重复支付：同上

**示例 curl**：
```bash
curl -X PUT http://localhost:8080/api/order/42/pay \
  -H "Authorization: Bearer $TOKEN"
```

### 发货订单（Phase 7 新增）
`PUT /api/order/{id}/ship`

**业务规则**：
- 仅 ADMIN(1) 或 SELLER(2) 可操作，**BUYER(3) 无权发货**
- 仅 `status=20`（已支付）的订单可以发货
- 成功后状态变为 30（已发货）
- 使用原子条件更新防止并发

**成功响应**：
```json
{
  "success": true,
  "code": 200,
  "message": "发货成功",
  "data": null
}
```

### 完成订单（Phase 7 新增）
`PUT /api/order/{id}/complete`

**业务规则**：
- BUYER 只能完成**自己的**已发货订单
- ADMIN 可完成任意已发货订单
- SELLER 可完成已发货订单
- 仅 `status=30`（已发货）的订单可以完成
- 成功后状态变为 40（已完成）
- 使用原子条件更新防止并发

**成功响应**：
```json
{
  "success": true,
  "code": 200,
  "message": "确认完成成功",
  "data": null
}
```

### 订单超时自动取消（Phase 8 新增）

本阶段**没有新增用户 API**。
系统通过后台定时任务（`@Scheduled`）自动扫描 `status=10` 且 `create_time` 超过配置阈值的订单，自动将状态改为 `status=50` 并恢复对应 SKU 库存。

---

## 11. 支付接口（Phase 9 新增）

### 创建支付单
`POST /api/payment/order/{orderId}`

**业务规则**：
- 必须登录
- 只能为自己的待支付订单创建支付单
- 幂等：已存在待支付支付单则直接返回
- 已支付/已取消订单不能创建

**成功响应**：
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "paymentNo": "PAY202605190001",
    "orderId": 1,
    "amount": 199.00,
    "status": 10
  }
}
```

### 模拟支付回调
`POST /api/payment/mock-callback`

**说明**：
- 模拟第三方支付平台异步回调
- 不要求登录，但必须携带正确 `mockSign`
- 支持幂等：重复 SUCCESS 回调不会重复修改订单
- 已取消订单不会被支付成功回调改状态

**请求示例**：
```json
{
  "paymentNo": "PAY202605190001",
  "orderId": 1,
  "paidAmount": 199.00,
  "payStatus": "SUCCESS",
  "mockSign": "demo027"
}
```

### 退款接口（Phase 10 新增）

`POST /api/payment/refund/{orderId}`

**业务规则**：
- 必须登录
- BUYER 只能退自己的已支付订单，ADMIN 可退任意，SELLER 可退
- 仅 status=20 已支付 + payment_order status=20 可退款
- 成功后 order 20→60，payment 20→40，恢复库存
- 条件更新 + 同一事务保证并发安全

**请求**：`POST /api/payment/refund/123`（带 Token）

**响应**：Result.success("退款成功", null)

---

## 12. 订单与支付状态机说明（Phase 10）

本阶段在 Phase 9 基础上扩展退款流转：

- **10（待支付）→ 20（已支付）**：推荐通过支付单 + mock-callback（Phase 9）
- **20（已支付）→ 60（已退款）**：Phase 10 新增，通过 `POST /api/payment/refund/{orderId}`（仅支持未发货订单，恢复库存）
- **20（已支付）→ 30（已发货）**：Phase 7 发货
- **10（待支付）→ 50（已取消）**：Phase 5/8 支持

退款与发货通过条件更新竞争（20→60 与 20→30 互斥），保证一致性。

完整状态机设计、退款事务边界、并发安全详见 [Phase10.md](./Phase10.md)。

---

**文档生成时间**：2026-05-19  
**验证状态**：demo028_mall Phase 10 模拟退款流程已完成，mvn package 通过，文档已自洽。
本阶段新增模拟退款流程：已支付未发货订单 20→60，支付单 20→40，并在同一事务内恢复库存，强调退款事务边界与条件更新并发安全。