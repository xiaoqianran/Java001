# demo030_mall Phase 12 演进记录 —— 部分退款与退款金额校验

> **前置**：demo029_mall 已完成 Phase 11 退款申请与审核流程（支持全额退款申请 + 审核 + 一订单一申请）。
> **本阶段目标**：在 Phase 11 基础上，增加对**退款金额**的支持。买家申请时必须填写 `refundAmount`，服务端进行金额校验，审核通过后按指定金额执行退款并记录 `refunded_amount`。
> 教学重点：金额校验、财务字段设计、事务边界、“一订单一申请”下的部分退款教学简化。
> 严格限制（本阶段）：不支持多次部分退款、不新增 refund_order_item、不做退货物流、不接真实支付渠道。

---

## 1. 为什么要支持部分退款金额？

真实业务场景中：
- 买家可能只对部分商品不满意（例如买了 3 件，只想退 1 件）
- 商家出于友好或协商，可能同意部分退款而不是全额
- 财务上需要精确记录本次退了多少钱（而非只知道“退过款”）

Phase 11 只支持“要么全退、要么不退”，无法满足“部分金额”场景。

Phase 12 的教学目标是：**在不改变“一订单一申请”规则的前提下，引入金额字段和校验**，为后续真正的多次部分退款打下基础。

---

## 2. 为什么本阶段仍不做多次部分退款？

教学原则：**每一步只做一个重点变化**。

- Phase 11 刚刚引入“申请 + 审核 + 执行分离”和“一个订单只能有一条退款申请”。
- 如果立刻支持多次部分退款，需要新增 `refund_order_item` 明细表、多次退款的并发控制、已退款金额累加校验等大量逻辑。
- 本阶段只聚焦**金额字段 + 校验 + 财务记录**，保持“一订单一申请”规则不变，降低复杂度。

后续演进（Phase 13+）再拆分为多次部分退款 + 明细行。

---

## 3. refund_order.refund_amount 字段设计

```sql
refund_amount  DECIMAL(10,2) NOT NULL COMMENT '申请退款金额'
```

- 由买家在申请时填写
- 在 `applyRefund` 中进行校验（>0 且 <= 支付金额）
- 审核通过后原样传递给退款执行逻辑
- 即使被拒绝，该金额也保留（用于审计）

---

## 4. payment_order.refunded_amount 字段设计

```sql
refunded_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '已退款金额'
```

- 记录该支付单**实际已退**的金额
- 审核通过时由 `PaymentService.refundOrder` 写入
- 本阶段由于“一订单一申请”，该字段最终要么是 0，要么等于本次 `refund_amount`
- 为未来多次部分退款累加做准备（`refunded_amount` 只会单调增加）

---

## 5. 申请退款时的金额校验规则（applyRefund）

在 `RefundServiceImpl.applyRefund` 中：

1. 订单必须属于当前 BUYER 且 status = 20
2. 必须存在 payment_order 且 status = 20
3. `refundAmount` 非空
4. `refundAmount > 0`
5. `refundAmount <= payment_order.amount`
6. 已有退款申请则拒绝（uk_order_id 唯一约束 + 业务校验）

只有全部通过才创建 `refund_order` 并保存 `refund_amount`。

---

## 6. 审核通过时如何把 refundAmount 传给 PaymentService

`RefundServiceImpl.approveRefund`：

```java
// 1. 先条件更新抢占（status=10 → 20）
int affected = ... WHERE id=? AND status=10 ...;

// 2. 抢占成功后才执行退款
paymentService.refundOrder(admin, refund.getOrderId(), refund.getRefundAmount());
```

如果 `refundOrder` 抛异常，事务回滚，`refund_order` 状态也会回到 10。

---

## 7. PaymentService 如何更新 refunded_amount

`PaymentServiceImpl.refundOrder(operator, orderId, refundAmount)`：

- 校验 `refundAmount` 非空、>0、<= payment.amount
- 条件更新 `payment_order`：
  ```sql
  UPDATE payment_order
  SET status = 40,
      refunded_amount = ?,
      callback_time = NOW()
  WHERE order_id = ? AND status = 20
  ```
- 条件更新 `order` 20 → 60
- 恢复库存
- 全部在同一个 `@Transactional` 中，任何失败整体回滚

---

## 8. 为什么即使是部分金额退款，订单仍进入 60 已退款终态？

本阶段的**教学简化**：
- 仍然遵守“一订单一申请”规则
- 部分退款只是**财务金额**上的部分（记录了退了多少钱）
- 业务上订单已结束（不再支持继续发货、继续退款）
- 这样可以复用 Phase 10/11 的状态机和库存恢复逻辑，不引入新的状态

真正支持“部分退款后还能继续操作”的场景，需要在后续版本中把订单状态与退款金额解耦（例如增加“部分退款中”状态 + 明细表）。

---

## 9. 后续如何演进到多次部分退款 + refund_order_item 明细？

- 新增 `refund_order_item` 表（关联 refund_order + sku + 数量 + 金额）
- 放宽 `refund_order` 的唯一约束，允许同一订单多条 `status=30`（已拒绝）或多条已通过记录
- `payment_order.refunded_amount` 改为累加
- 每次申请时校验 `refunded_amount + 本次金额 <= 支付金额`
- 引入部分退款后的订单状态流转（例如 20 → 65 部分退款 等）

Phase 12 为以上演进做了最小的字段和校验铺垫。

---

**本阶段交付标准**：
- 买家申请时必须填写合法的 refundAmount
- 金额校验在申请和执行两处都生效
- 审核通过后 `payment_order.refunded_amount` 正确写入
- mvn package 通过，文档自洽

下一步演进方向：多次部分退款 + 退款明细行 + 真实支付渠道退款回调。
