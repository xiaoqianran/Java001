# demo029_mall Phase 11 演进记录 —— 退款申请与审核流程

> **前置**：前一版本已完成 Phase 10 模拟退款流程（直接退款 POST /api/payment/refund/{orderId}）。
> **本阶段目标**：引入退款申请（refund_order）与审核工作流，实现「买家申请 → 管理员审核 → 通过后执行退款」的完整流程。强调申请与执行分离、审批流、并发防护（同一订单不可重复申请）、权限边界。
> 严格限制（本阶段）：全额退款 + 仅支持已支付未发货(20)订单申请；拒绝部分退款、已发货退款、多次退款；不接真实支付渠道退款 API。

---

## 1. 为什么需要退款申请 + 审核流程？

Phase 10 提供了**直接退款**能力（适合客服/管理员直接操作）。

但在真实业务中：
- 买家需要“发起退款申请”的自助入口（附退款原因）
- 不是所有申请都自动通过，需要客服/卖家审核（防止滥用、核实情况）
- 需要留痕：谁申请的、什么原因、何时审核、审核意见
- 便于对账、客诉追踪、数据统计

**教学价值**：
- 学习领域建模：新增 RefundOrder 聚合
- 掌握“申请(草稿/待审) → 审核(通过/拒绝) → 执行(副作用：状态+库存)”的典型工作流事务边界
- 条件更新 + 唯一性约束防止重复申请
- 角色权限在 Service 层的细化（BUYER 只能对自己订单申请，ADMIN 审核任意）
- 为后续“部分退款 + 退款明细行”、“退货物流逆向单”、“真实网关退款回调”做铺垫

---

## 2. 核心设计决策

### 2.1 新增 refund_order 表而非复用 payment_order

- payment_order 记录“正向支付事实”（一次支付一个单）
- refund_order 记录“逆向退款申请与结果”（一次申请对应一次或零次执行）
- 支持未来：一个订单多次部分退款（多条 refund_order）
- 清晰的申请状态（10/20/30）与最终退款结果解耦

### 2.2 退款申请状态

- 10：待审核（买家刚提交，等待 ADMIN 操作）
- 20：已通过（审核通过，已触发退款执行，订单/支付单已变更）
- 30：已拒绝（审核拒绝，附拒绝原因，订单状态不变）

### 2.3 申请与执行的边界

- 申请接口：仅创建 refund_order (status=10)，不改订单/支付/库存
- 审核通过接口：校验申请状态=10 → 调用退款核心服务（复用或提取 Phase 10 的 refund 执行逻辑）→ 更新申请 status=20 + 记录审核人/时间/备注
- 整个过程用 @Transactional 包裹，失败则申请不标记通过

### 2.4 并发与幂等

- 数据库唯一约束：`UNIQUE KEY uk_order (order_id)` 或更宽松的“进行中申请唯一”
- 或者应用层 + DB：同一订单只能存在 status=10 的申请（待审中不能再申请）
- 审核通过使用条件更新保证只执行一次

---

## 3. 表结构（新增）

```sql
CREATE TABLE `refund_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '退款申请ID',
  `order_id` BIGINT UNSIGNED NOT NULL COMMENT '关联订单ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '申请人用户ID（BUYER）',
  `reason` VARCHAR(255) NOT NULL COMMENT '退款原因',
  `status` TINYINT NOT NULL DEFAULT 10 COMMENT '10-待审核 20-已通过 30-已拒绝',
  `apply_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `review_time` DATETIME NULL COMMENT '审核时间',
  `reviewer_id` BIGINT UNSIGNED NULL COMMENT '审核人ID（ADMIN/SELLER）',
  `review_remark` VARCHAR(255) NULL COMMENT '审核备注/拒绝原因',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_pending` (`order_id`, `status`)  -- 简化：同一订单仅允许一个待审申请（视业务可调整为仅一个非拒绝）
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请单';
```

> 注：实际可根据是否允许多次申请调整唯一约束。本阶段先实现“一个订单最多一个非拒绝申请”。

---

## 4. 新增接口（规划）

| 方法 | 路径 | 角色 | 说明 |
|------|------|------|------|
| POST | /api/refund/apply/{orderId} | BUYER | 提交退款申请（body: {reason}） |
| GET  | /api/refund/my | BUYER | 我的退款申请列表（分页可选） |
| GET  | /api/refund | ADMIN/SELLER | 待审核列表 + 条件筛选 |
| POST | /api/refund/{refundId}/approve | ADMIN/SELLER | 审核通过（可选 body remark） |
| POST | /api/refund/{refundId}/reject | ADMIN/SELLER | 审核拒绝（body: {reason}） |

---

## 5. 实现步骤（本阶段拆分提交计划）

1. chore: 初始化 demo029_mall（基于前一版本 Phase 10 拷贝 + 文档基线）
2. feat: 新增 refund_order 表到 sql/init.sql + 演示数据
3. feat: 新增 RefundOrder 实体、RefundOrderMapper
4. feat: 新增 RefundService + RefundServiceImpl（申请、列表、审核核心）
5. feat: 新增 RefundController（5 个接口 + 权限集成）
6. feat: 复用/提取退款执行逻辑（从 PaymentService 或新建共享），支持从审核调用
7. refactor: 调整原有 /payment/refund 接口说明（或保留作为管理员直达入口）
8. docs: 完善 Phase11.md、API-接口文档.md、README.md 最终自洽 + mvn package 验证

---

## 6. 权限设计延续

- Controller 薄层：只拿 LoginUser
- Service 负责：
  - BUYER：只能对自己 user_id 的订单申请、只能查自己 refund_order
  - ADMIN/SELLER：可查所有待审、可审核任意
- 订单归属校验 + 状态校验（必须是 20 + 无进行中 refund_order）

---

## 7. 后续可演进（不属于本 Phase）

- 部分退款（refund_amount + refund_order_item 明细表）
- 退款凭证图片上传（多媒体模块）
- 对接真实支付网关（refund API + 异步通知处理）
- 退货（reverse logistics） + 逆向库存
- 自动退款（超时未审自动通过等策略）

---

**本阶段交付标准**：
- 买家可成功对符合条件的订单提交退款申请
- 管理员可查询待审列表、通过/拒绝
- 通过后订单、支付单、库存三者一致变更，且申请状态正确流转
- 各种非法场景（重复申请、状态不对、越权）均被正确拒绝
- mvn clean package 成功，API 文档与代码自洽

---

*Phase 11 已完成，mvn package 通过，文档自洽。*
