# demo023_mall Phase 5 演进记录 —— 订单状态机与取消订单

> **前置**：demo022_mall Phase 4 已完成订单基础闭环（创建订单 + 事务扣库存 + 清购物车 + 查询）。
> **本阶段目标**：在已有订单模型上，引入**显式状态机**，实现「取消订单 + 库存回滚」这一真实业务场景。
> **不做**：支付、发货、完成流转、Redis、MQ（留待后续）。

---

## 1. 为什么引入订单状态机？

### 问题背景（Phase 4 遗留）
- 订单 `status` 只是 `Integer`，散落在各处使用魔法数字 `10/20/50`
- 创建订单后，任何代码都可以直接 `order.setStatus(99)`，**没有规则约束**
- 取消订单需求一旦出现：
  - 谁能取消？什么状态能取消？
  - 取消后库存怎么安全回滚？
  - 并发下如何防止状态错乱？
- 如果不建立状态机，未来支付、发货、售后会变成一团乱麻，bug 频发。

### 状态机的价值
- **集中规则**：所有流转判断只在 `OrderStatus` 枚举一处维护
- **可读性**：`if (status.canCancel())` 远胜 `if (status == 10)`
- **可扩展性**：新增 `canPay()`、`canShip()` 非常自然
- **教学价值**：真实电商系统里「状态机 + 事务」是高频难点，本阶段用最小闭环讲透

---

## 2. 保留的状态定义（与 Phase 4 完全兼容）

| 状态码 | 枚举名            | 描述   | 说明                     |
|--------|-------------------|--------|--------------------------|
| 10     | PENDING_PAYMENT   | 待支付 | 初始状态，下单成功即为此 |
| 20     | PAID              | 已支付 | 支付成功（本阶段不实现） |
| 30     | SHIPPED           | 已发货 | 仓库已出（本阶段不实现） |
| 40     | COMPLETED         | 已完成 | 终态，用户确认收货       |
| 50     | CANCELLED         | 已取消 | 终态，库存已回滚         |

**终态规则**：`40` 和 `50` 一旦进入，不允许任何变更。

---

## 3. Phase 5 状态流转规则（白名单）

本阶段**只实现一条合法流转**：

- `10 (待支付)` → `50 (已取消)` ：**取消订单**（唯一开放路径）

**禁止的流转（全部抛 BusinessException）**：
- 已支付/已发货/已完成/已取消 → 取消
- 任何状态直接跳到 20/30/40（本阶段无对应接口）
- 终态再次变更

```java
// 核心判断集中在这里
public boolean canCancel() {
    return this == PENDING_PAYMENT;
}
```

---

## 4. 取消订单接口设计

### 接口
`PUT /api/order/{id}/cancel`

### 安全与权限（绝不信任前端）
1. `SecurityUtils.getCurrentUser(request)` 取 `userId`
2. 未登录 → `BusinessException(401, "请先登录")`
3. 订单不存在或 `user_id != 当前用户` → `BusinessException(403, "订单不存在或无权操作该订单")`
4. 状态不允许 → 明确提示当前状态

### 业务流程（Service 层）
1. 按 `(id, userId)` 查订单（双条件防越权）
2. `OrderStatus.fromCode(order.getStatus()).canCancel()` 校验
3. 查 `order_item` 列表
4. **循环调用 `skuService.restoreStock(skuId, qty)`**
5. 任一 restore 失败（乐观锁冲突或异常）→ 整个 `@Transactional` 回滚
6. 所有库存回滚成功后，才 `order.setStatus(50)` + `updateById`
7. 返回成功

---

## 5. 取消订单事务边界（最重要教学点）

```java
@Transactional(rollbackFor = Exception.class)
public void cancelOrder(Long userId, Long orderId) {
    // 1. 查订单 + 权限
    // 2. 状态机判断
    // 3. 查 order_item
    for (item) {
        skuService.restoreStock(...)   // 内部有乐观锁 + 失败抛异常
    }
    order.setStatus(50);
    orderMapper.updateById(order);
    // 4. 方法结束 → 事务提交
}
```

**为什么必须同一个事务？**
- 假设先把 status 改成 50，再回滚库存 → 库存回滚失败，订单已变成「已取消」但货还在 → **数据不一致**
- 反之：先回库存，改状态失败 → 状态还是 10，但库存多了 → 用户可重复下单或库存异常
- **结论**：库存回滚 + 状态变更 必须原子，要么全成功，要么全回滚。

`restoreStock` 内部同样使用 MyBatis-Plus `@Version` 乐观锁，与 `reduceStock` 保持一致。

---

## 6. 为什么「只能取消待支付订单」？

1. **业务常识**：已支付的订单，钱已经收了（或第三方支付有记录），不能简单「取消」了事，需要走退款流程（Phase 6+ 再做）。
2. **库存一致性**：已支付后可能已进入仓库拣货、已发货，取消需要更复杂的「逆向物流 + 退款」流程。
3. **教学递进**：先把「最简单、最干净」的取消场景（未支付、无需退款）做扎实，再逐步增加复杂度。
4. **防止滥用**：如果所有状态都能取消，恶意用户可大量占用库存后取消，系统被攻击。

---

## 7. 实现亮点与规范遵循

- 新增 `module/order/enums/OrderStatus.java`（单一事实来源）
- Service 层做状态判断，Controller 只负责 HTTP + 取 userId
- 所有新代码带高质量中文教学注释
- 严格使用 `SecurityUtils`，绝不从 DTO/路径拿 userId
- 增强了 `SkuService.restoreStock` 的失败判断（与 reduceStock 对齐）
- 文档先行：Phase5.md + API 文档同步更新

---

## 8. 验证与后续

```bash
# 构建验证
mvn -q -DskipTests package

# 功能验证（需先登录拿 token）
PUT /api/order/123/cancel
# 期望：
#  - 10 态订单 → 200 取消成功，库存 + 回，status=50
#  - 20/50 态订单 → 业务错误
#  - 别人订单 → 403
```

**本阶段交付标准**：
- 能成功取消待支付订单
- 库存正确回滚
- 非法状态/越权全部被拒绝且有明确提示
- 事务一致性通过测试（可手动模拟并发）

---

**demo023_mall Phase 5 订单状态机 + 取消订单已完成，可作为 PR 提交。**
