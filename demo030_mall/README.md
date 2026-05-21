# demo030_mall - 小型电商系统演进版（Phase 12：部分退款与退款金额校验）

> **核心教学理念**：严格遵循「每一步只做一个重点变化」。
> 从 demo020 的成熟骨架出发，一步一步构建真正有业务价值的电商系统。
> 每个版本（demo021_mall → demo022_mall → demo023_mall → demo024_mall …）都是在上一个版本基础上**小步升级**。

> **当前状态**：Phase 12 已完成 —— 部分退款与退款金额校验（买家申请时填写 refundAmount，服务端校验金额范围，审核通过后按指定金额退款并记录 refunded_amount）。

---

## 项目定位

`demo030_mall` 是整个教学系列的**部分退款金额演进版本**：

- **demo001 ~ demo020**：以「学生管理系统」为载体，系统性地学习 JDBC → MyBatis → Spring Boot → 生产级基础设施。
- **demo021_mall**：Phase 1-3（用户认证 + 商品域 + 购物车）。
- **demo022_mall**：Phase 4（订单基础闭环：创建订单 + 扣减库存 + 清购物车）。
- **demo023_mall**：Phase 5（订单状态机 + 取消订单 10 → 50）。
- **demo024_mall**：Phase 6（简单支付模拟：待支付订单 10 → 20）。
- **demo025_mall**：Phase 7（发货与完成：已支付 20 → 已发货 30 → 已完成 40）。
- **demo026_mall**：Phase 8（订单超时自动取消：待支付订单超时自动取消并恢复库存）。
- **demo027_mall**：Phase 9（模拟第三方支付回调：通过支付单 + mock-callback 驱动订单 10 → 20，强调幂等性）。
- **demo028_mall**：Phase 10（模拟退款流程：已支付未发货 20 → 60 已退款 + 支付单退款 + 库存恢复）。
- **demo029_mall**：Phase 11（退款申请与审核流程：申请(待审/通过/拒绝) + 审核触发执行 + 并发与权限控制）。
- **demo030_mall**：Phase 12（部分退款与退款金额校验：申请时携带 refundAmount，服务端金额校验 + 按指定金额退款）。

**本版本（demo030_mall）** 基于 Phase 11 基线，开发 **Phase 12：部分退款与退款金额校验**。

---

## 当前已实现功能总览

| Phase | 领域             | 关键功能                                      | 教学重点                     |
|-------|------------------|-----------------------------------------------|------------------------------|
| **1** | 用户与认证       | 注册、JWT 登录、RBAC 权限                     | 无状态认证 + Spring Security |
| **2** | 商品域           | Category + SPU + SKU（乐观锁库存）            | 领域建模 + 乐观锁            |
| **3** | 购物车           | 添加、修改、删除、列表（实时关联 SKU）        | 关联查询 + 业务规则          |
| **4** | 订单基础         | 创建订单（事务 + 扣库存 + 清购物车）          | @Transactional + 一致性      |
| **5** | 订单状态机       | 取消订单（10 → 50，原子更新 + 库存回滚）      | 状态机设计 + 事务边界        |
| **6** | 简单支付模拟     | 支付订单（10 → 20，原子条件更新防重复支付）   | 状态流转 + 幂等性            |
| **7** | 发货与完成       | 发货（20 → 30）、完成（30 → 40），角色权限控制 | 状态机 + 权限边界            |
| **8** | 超时自动取消     | 待支付订单超时自动取消（10 → 50）+ 恢复库存     | @Scheduled + 并发安全        |
| **9** | 支付回调与幂等   | 支付单 + mock-callback 驱动 10 → 20，幂等处理   | 支付模型 + 回调事务边界      |
| **10** | 模拟退款         | 已支付未发货 20 → 60（已退款），支付单 20 → 40 + 恢复库存 | 退款事务边界 + 条件更新并发安全 |
| **11** | 退款申请与审核   | 买家申请退款(10待审/20通过/30拒绝) + 管理员审核执行退款 | 工作流 + 审批流 + 申请与执行分离 |
| **12** | 部分退款与金额校验 | 申请时填写 refundAmount，服务端校验（>0 且 <=支付金额），审核通过按指定金额退款 | 金额校验 + 财务金额记录 |

**已具备的生产级能力**：
- Result<T> 统一返回 + BusinessException + 全局异常
- LoggingFilter + MDC traceId
- MyBatis-Plus + 逻辑删除 + 乐观锁
- Spring Security + JWT
- Docker Compose（MySQL + Redis）

---

## 快速启动

```bash
cd demo030_mall
docker compose up -d
mvn clean spring-boot:run
```

访问：
- 健康检查：http://localhost:8080/api/health
- Actuator：http://localhost:8080/actuator/health

---

## 本阶段新增（Phase 12）

- 在 refund_order 表新增 `refund_amount` 字段：记录买家申请的退款金额
- 在 payment_order 表新增 `refunded_amount` 字段：记录该支付单已退款的总金额
- 买家申请退款时必须填写 `refundAmount`（必填、>0、<= 支付金额）
- 服务端在 applyRefund 中进行严格金额校验
- 审核通过后按申请的 refundAmount 执行退款，并更新 payment_order.refunded_amount
- 文档与接口均要求明确标注本阶段**不支持多次部分退款**（一订单一申请规则保持不变）

---

## 状态流转（新增）

- 20（已支付）→ 60（已退款）：Phase 10/11 已实现
- Phase 12 新增：支持指定 refundAmount（部分或全额），但订单仍进入 60 终态（本阶段教学简化）

---

## 阶段性文档

- [Phase1-3.md](./Phase1-3.md)（历史基线）
- [Phase4.md](./Phase4.md)（订单基础闭环）
- [Phase5.md](./Phase5.md)（状态机 + 取消订单）
- [Phase6.md](./Phase6.md)（简单支付模拟）
- [Phase7.md](./Phase7.md)（发货与完成状态流转）
- [Phase8.md](./Phase8.md)（订单超时自动取消）
- [Phase9.md](./Phase9.md)（模拟第三方支付回调与支付幂等）
- [Phase10.md](./Phase10.md)（模拟退款流程 - 基线）
- [Phase11.md](./Phase11.md)（退款申请与审核流程 - 历史基线）
- [Phase12.md](./Phase12.md)（部分退款与退款金额校验 - 本阶段已完成）
- [API-接口文档.md](./API-接口文档.md)

**demo030_mall Phase 12 部分退款与退款金额校验已完成（基于 Phase 11 完整基线）。**

下一步演进方向：多次部分退款 + refund_order_item 明细、真实支付渠道退款回调、退货物流等。
