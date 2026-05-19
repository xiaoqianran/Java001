# demo022_mall Phase 4 演进记录 —— 订单系统基础闭环（demo023_mall 继承此基线）

## 目标

在 demo021_mall（Phase 1-3）基础上，实现可真实使用的**下单交易闭环**。

核心教学点：
- 事务边界设计（一个方法内完成多表操作 + 外部服务调用）
- 乐观锁在真实业务场景的应用
- 领域建模（Order + OrderItem + 快照）
- 认证与权限一致性

## 主要新增内容

### 数据库
- `order` 表（订单主表）
- `order_item` 表（订单明细，带商品快照）

### Java 模块
- `module/order/`
  - `entity/Order.java`、`OrderItem.java`
  - `mapper/`
  - `dto/OrderCreateDTO.java`、`OrderItemDTO.java`
  - `vo/OrderVO.java`、`OrderItemVO.java`
  - `service/OrderService.java` + `impl/OrderServiceImpl.java`（含 `@Transactional`）
  - `controller/OrderController.java`

### 增强
- `CartService.removeCartItems(userId, skuIds)` 支持批量清理

## 下单事务边界

`OrderServiceImpl.createOrder()` 使用 `@Transactional` 保证：
1. 合并重复 SKU 数量
2. 校验 SKU 状态 + 库存
3. 调用 `SkuService.reduceStock()`（乐观锁）
4. 生成订单号（带重试）
5. 保存 `order` + `order_item`（快照）
6. 调用 `cartService.removeCartItems()` 清购物车

任一失败 → 整体回滚。

## 已完成的健全性修复

- SecurityUtils 正确处理 `LoginUser` principal
- 新增 `MybatisPlusConfig`（分页 + 乐观锁插件）
- 删除 `UserDebugController`，保护 `/api/user/list`
- 订单创建逻辑强化（重复 SKU 合并、严格异常）
- 文档全面自洽（无 demo021_mall 残留）

## 验证命令

```bash
# 构建
mvn -q -DskipTests package

# 运行
docker compose up -d
mvn spring-boot:run

# 验证示例
curl -X POST /api/auth/login ...
curl -H "Authorization: Bearer $TOKEN" -X POST /api/order ...
```

---

**demo022_mall Phase 4 订单基础闭环已完成（作为 demo023_mall 的历史基线保留）。**