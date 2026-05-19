# demo022_mall Phase 4 演进记录 —— 订单系统基础闭环（demo023_mall 继承此基线）

> **本文件为历史基线文档**。demo022_mall 实现了 Phase 4 订单基础闭环，demo023_mall 完全继承该实现并在其上继续演进。

---

## 目标

在 demo021_mall（Phase 1-3：用户认证 + 商品 + 购物车）基础上，实现可真实使用的**下单交易闭环**。

### 核心教学点
- 事务边界设计（一个 Service 方法内完成多表 + 跨服务调用）
- 乐观锁在真实电商场景（库存扣减）的应用
- 领域建模（Order + OrderItem + 商品快照）
- 认证与权限一致性（SecurityUtils）
- 数据一致性保障（任一环节失败全部回滚）

---

## 主要新增内容

### 1. 数据库表（sql/init.sql）
- `order` 表：订单主表（order_no、user_id、total_amount、status、create_time...）
- `order_item` 表：订单明细表（带 sku 快照：sku_name、sku_specs、price、quantity）

### 2. Java 模块（module/order/）
- **entity**: `Order.java`、`OrderItem.java`
- **mapper**: `OrderMapper.java`、`OrderItemMapper.java`
- **dto**: `OrderCreateDTO.java`、`OrderItemDTO.java`
- **vo**: `OrderVO.java`、`OrderItemVO.java`
- **service**: `OrderService.java` + `OrderServiceImpl.java`（`@Transactional` 核心）
- **controller**: `OrderController.java`

### 3. 现有模块增强
- `SkuService.reduceStock(skuId, qty)` —— 乐观锁扣减（version 校验，失败抛异常）
- `CartService.removeCartItems(userId, skuIdList)` —— 批量清购物车

---

## 下单核心流程（createOrder）

`OrderServiceImpl.createOrder(Long userId, OrderCreateDTO dto)` 是 Phase 4 的核心方法：

1. **参数合并**：同一 SKU 的数量合并，防止重复下单同一商品
2. **SKU 校验**：批量查询、状态检查（status=1 在售）
3. **库存扣减**：循环调用 `skuService.reduceStock()`（乐观锁）
   - 任一 SKU 库存不足或版本冲突 → 立即抛 `BusinessException`，事务回滚
4. **生成订单号**：时间戳 + 随机 + 重试防冲突
5. **持久化**：
   - 插入 `order`（status=10 待支付）
   - 插入多条 `order_item`（保存商品快照）
6. **清购物车**：`cartService.removeCartItems(userId, mergedSkuIds)`
7. **返回**：新订单 ID

**事务边界**：整个方法用 `@Transactional(rollbackFor = Exception.class)` 包裹，**要么全部成功，要么全部回滚**。

---

## 订单查询

- `GET /api/order` —— 当前用户订单分页列表（关联 order_item + SKU 快照）
- `GET /api/order/{id}` —— 订单详情（权限校验：只能看自己的）

---

## 验证与交付标准

```bash
mvn -q -DskipTests package
docker compose up -d
# 登录后下单
curl -H "Authorization: Bearer $TOKEN" -X POST /api/order -d '{...}'
```

**Phase 4 交付标准**：
- 能成功创建订单
- 库存正确扣减（乐观锁生效）
- 购物车对应商品被清空
- 下单失败时数据库完全一致（无半状态）
- 所有接口都有良好的异常返回

---

**demo022_mall Phase 4 订单基础闭环已完成（作为 demo023_mall 的历史基线完整保留）。**
**demo023_mall 在此基础上继续实现 Phase 5 订单状态机与取消订单。**
