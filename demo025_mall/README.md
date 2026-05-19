# demo025_mall - 小型电商系统演进版（Phase 7：订单发货与完成状态流转）

> **核心教学理念**：严格遵循「每一步只做一个重点变化」。
> 从 demo020 的成熟骨架出发，一步一步构建真正有业务价值的电商系统。
> 每个版本（demo021_mall → demo022_mall → demo023_mall → demo024_mall …）都是在上一个版本基础上**小步升级**。

> **当前状态**：Phase 7 已完成 —— 订单发货与完成状态流转（20 → 30 → 40）。继承 demo024_mall 的支付模拟能力。

---

## 项目定位

`demo025_mall` 是整个教学系列的**订单状态流转演进版本**：

- **demo001 ~ demo020**：以「学生管理系统」为载体，系统性地学习 JDBC → MyBatis → Spring Boot → 生产级基础设施。
- **demo021_mall**：Phase 1-3（用户认证 + 商品域 + 购物车）。
- **demo022_mall**：Phase 4（订单基础闭环：创建订单 + 扣减库存 + 清购物车）。
- **demo023_mall**：Phase 5（订单状态机 + 取消订单 10 → 50）。
- **demo024_mall**：Phase 6（简单支付模拟：待支付订单 10 → 20）。
- **demo025_mall**：Phase 7（发货与完成：已支付 20 → 已发货 30 → 已完成 40）。

**本版本（demo025_mall）** 已完成 **Phase 7：订单发货与完成状态流转**。

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

**已具备的生产级能力**：
- Result<T> 统一返回 + BusinessException + 全局异常
- LoggingFilter + MDC traceId
- MyBatis-Plus + 逻辑删除 + 乐观锁
- Spring Security + JWT
- Docker Compose（MySQL + Redis）

---

## 快速启动

```bash
cd demo025_mall
docker compose up -d
mvn clean spring-boot:run
```

访问：
- 健康检查：http://localhost:8080/api/health
- Actuator：http://localhost:8080/actuator/health

---

## 新增接口（Phase 7）

- `PUT /api/order/{id}/pay` —— 模拟支付成功（10 → 20）
- `PUT /api/order/{id}/ship` —— 发货（20 → 30，ADMIN/SELLER 操作）
- `PUT /api/order/{id}/complete` —— 确认完成（30 → 40）

---

## 状态流转

- 10（待支付）→ 50（已取消）：Phase 5 已实现
- 10（待支付）→ 20（已支付）：Phase 6 已实现
- 20（已支付）→ 30（已发货）：Phase 7 新增（发货）
- 30（已发货）→ 40（已完成）：Phase 7 新增（确认收货/完成）

---

## 阶段性文档

- [Phase1-3.md](./Phase1-3.md)（历史基线）
- [Phase4.md](./Phase4.md)（订单基础闭环）
- [Phase5.md](./Phase5.md)（状态机 + 取消订单）
- [Phase6.md](./Phase6.md)（简单支付模拟）
- [Phase7.md](./Phase7.md)（发货与完成状态流转）
- [API-接口文档.md](./API-接口文档.md)

**demo025_mall Phase 7 订单发货与完成状态流转已完成。**

下一步建议：订单超时自动取消、支付回调模拟、真实物流/退款（三者后续择一）。
