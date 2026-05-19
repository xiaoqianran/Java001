# demo026_mall Phase 8 演进记录 —— 订单超时自动取消

> **前置**：demo025_mall 已完成 Phase 7 订单发货与完成状态流转（20 → 30 → 40）。
> **本阶段目标**：实现待支付订单超时自动取消机制，保护库存不被长期占用。
> **严格限制**：仅使用 Spring Boot 自带 @Scheduled，不引入 Redis、MQ、XXL-JOB 等。

---

## 1. 为什么需要订单超时自动取消？

在真实电商系统中，如果用户下单后长时间不支付，会导致以下问题：

- 库存被长期锁定，其他用户无法购买
- 商家无法及时释放商品
- 恶意下单占用库存

**教学价值**：
- 学习 Spring `@Scheduled` 定时任务
- 学习批量处理 + 事务边界控制
- 学习并发安全设计（支付、主动取消、自动取消三者竞争）
- 为后续引入延迟消息队列（RabbitMQ/RocketMQ）或分布式任务调度做铺垫

---

## 2. 配置设计

在 `application.yml` 中配置：

```yaml
mall:
  order:
    timeout-minutes: 30                    # 超时时间
    timeout-scan-fixed-rate-ms: 60000      # 扫描频率
    timeout-batch-size: 100                # 每次处理上限
```

通过 `OrderTimeoutProperties` 注入，避免硬编码。

---

## 3. 定时任务设计

- 使用 `@EnableScheduling` + `@Scheduled(fixedRate = ...)`
- 每次扫描 `status=10` 且 `create_time <= now - timeoutMinutes` 的订单
- 限制单次处理数量（`timeoutBatchSize`），防止一次扫太多导致长时间事务
- 调用 `OrderService.cancelTimeoutOrders()` 返回本次成功取消数量

---

## 4. 超时取消事务边界（核心）

`cancelTimeoutOrders()` 中：

1. 查询一批超时订单
2. 对每个订单调用内部 `cancelTimeoutOrder(orderId)`
3. 内部方法使用 `@Transactional`
4. 先执行条件更新把 status 10 → 50
5. 更新成功后再恢复库存（调用 `skuService.restoreStock`）
6. 任一库存恢复失败 → 整个事务回滚，状态也回 10

**为什么先更新状态再恢复库存？**
- 防止“状态已取消但库存恢复失败”导致数据不一致
- 条件更新保证只有真正超时的订单才会被处理

---

## 5. 并发安全策略

支付、用户主动取消、系统自动取消三者可能同时发生。

解决方案：
- 所有取消操作（包括超时）都使用条件更新：
  ```sql
  UPDATE `order` SET status = 50 
  WHERE id = ? AND status = 10 AND create_time <= ?
  ```
- 影响行数 != 1 时跳过，不抛异常（避免一个订单失败导致整批任务失败）

---

## 6. 库存恢复

复用 `SkuService.restoreStock(skuId, quantity)`。

- 超时取消成功后，查询 `order_item`
- 逐个恢复库存
- 失败则事务回滚

---

## 7. 日志要求

每次定时任务执行打印：

- 本次扫描开始
- 超时时间阈值
- 扫描到多少待处理订单
- 成功取消多少
- 失败多少

异常不能吞掉，必须记录。

---

## 8. 后续演进方向

- 使用 Redis + Lua 脚本做更高效的超时判断
- 引入延迟消息队列（RabbitMQ 死信队列 / RocketMQ 延迟消息）
- 迁移到 XXL-JOB / Elastic-Job 等分布式任务调度平台
- 支持不同商品不同超时时间

---

**本阶段交付标准**：
- 能正确自动取消超时的待支付订单
- 库存正确回滚
- 并发场景下不出现状态不一致
- 日志清晰

**demo026_mall Phase 8 订单超时自动取消已完成。**
