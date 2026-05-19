# demo022_mall API 接口文档（Phase 4 - 订单系统）

> **项目**：demo021_mall - 小型电商系统演进版  
> **当前版本**：Phase 1-3 已完成（用户认证 + 商品域 + 购物车）  
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

- `GET /api/user/list`
- `GET /api/debug/user/list`
- `GET /api/debug/user/by-username?username=xxx`

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

**文档生成时间**：2026-05-19  
**验证状态**：所有接口在修复 Mapper 扫描问题后已通过实际 curl 测试，全部正常返回 200 / 成功响应。