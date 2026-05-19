# demo022_mall 需求分析与演进规划

> **前置条件**：demo021_mall 已完成（Phase 1-3：用户认证 + 商品 + 购物车）  
> **文档目的**：帮助清晰定义 demo022_mall 的范围、教学目标和技术实现路径

---

## 1. 推荐主题

**demo022_mall 主题：订单系统（Order + 交易闭环）**

这是目前**最有教学价值和业务价值**的下一个版本。

### 为什么强烈推荐订单系统作为 demo022？

1. **业务闭环形成**  
   用户终于可以从「加购物车」走到「真正下单购买」，形成完整交易链路。

2. **事务教学价值极高**  
   这是第一个真正需要强一致性的事务场景：
   - 创建订单
   - 扣减 SKU 库存（必须使用乐观锁）
   - 清空对应购物车商品
   - 任一环节失败必须整体回滚

3. **领域建模能力提升**  
   需要设计 Order（订单主表）和 OrderItem（订单项）的一对多关系，这是真实电商系统的核心模型。

4. **与现有能力完美衔接**  
   - 可直接复用现有的 `SkuService.reduceStock()`（乐观锁）
   - 可复用 `CartService` 的删除逻辑
   - 可复用当前的用户认证体系

5. **为后续版本铺路**  
   订单状态机、支付模拟、退款、分布式事务等功能都必须建立在订单模块之上。

---

## 2. 核心功能拆分建议（Phase 4）

建议将 demo022_mall 拆分为以下几个有明确教学价值的 Step：

| Step | 功能                         | 教学重点                                   | 难度 |
|------|------------------------------|--------------------------------------------|------|
| 4.1  | 订单领域模型设计             | Order + OrderItem 表结构与实体设计         | 中   |
| 4.2  | 创建订单接口（基础版）       | @Transactional、参数校验、生成订单号       | 高   |
| 4.3  | 下单时扣减库存               | 复用现有乐观锁 + 事务边界控制              | 高   |
| 4.4  | 下单后清空购物车             | 业务规则与事务一致性                       | 中   |
| 4.5  | 订单列表与详情查询           | 关联查询（Order + OrderItem + SKU/SPU）    | 中   |
| 4.6  | 简单订单状态流转             | 状态枚举 + 状态变更方法（基础状态机）      | 中   |

**建议 demo022_mall 主要交付**：Step 4.1 ~ 4.4（能真正下单成功即可），状态机可作为 4.6 收尾或留到 demo023。

---

## 3. 涉及的新增内容

### 3.1 数据库表（建议在 sql/init.sql 中追加）

```sql
-- Phase 4 Step 1: 订单主表
CREATE TABLE `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1=待支付,2=已支付,3=已发货,4=已完成,5=已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- Phase 4 Step 1: 订单项表
CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    sku_name VARCHAR(200),
    sku_specs JSON,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 3.2 新增 Java 模块

```
module/order/
├── controller/
│   └── OrderController.java
├── dto/
│   └── OrderCreateDTO.java
├── entity/
│   ├── Order.java
│   └── OrderItem.java
├── mapper/
│   ├── OrderMapper.java
│   └── OrderItemMapper.java
├── service/
│   ├── OrderService.java
│   └── impl/
│       └── OrderServiceImpl.java
└── vo/
    └── OrderVO.java
```

### 3.3 需要修改/增强的现有模块

- **SkuService**：可能需要增强 `reduceStock` 的并发处理提示，或增加 `checkStock` 方法
- **CartService**：增加按用户+SKU列表批量删除的方法（下单成功后清空）
- **SecurityUtils**：确保 Service 层能方便获取当前 userId（已有，需确认健壮性）

---

## 4. 技术挑战点（教学重点）

1. **事务边界设计**（最重要）
   - `@Transactional` 应该放在 Service 层哪个方法上？
   - 异常回滚策略（默认 RuntimeException）

2. **乐观锁在真实交易中的使用**
   - 下单时如果库存不足或版本冲突，如何给用户友好提示？
   - 是否需要重试机制？

3. **数据一致性**
   - 订单创建成功 → 必须扣库存成功 → 必须清空购物车
   - 任一失败都要回滚

4. **订单编号生成**
   - 简单方式：时间戳 + 用户ID + 随机数
   - 教学中是否引入分布式 ID（暂不建议，保持简单）

5. **查询性能**
   - 订单列表是否需要分页？（建议带上，巩固 MyBatis-Plus 分页）

---

## 5. 与 demo021_mall 的衔接策略

- **目录结构延续**：继续使用 `module/order` 目录
- **代码风格延续**：所有新类都要有高质量中文教学注释
- **文档延续**：
  - 在 `demo021_mall.md` 中更新「下一步计划」
  - 新建 `demo022_mall/Phase4.md` 记录演进细节
  - 更新根目录的 `demo021_mall.md`（或新建 `demo022_mall.md`）

- **Git 流程延续**：
  - 创建 `feature/demo022_mall` 分支
  - 提交使用 conventional commit 规范
  - PR + 自动合并

---

## 6. 建议的 demo022_mall 交付标准

一个合格的 demo022_mall 应该能做到：

- 用户登录后可以基于购物车下单
- 下单成功后：
  - 生成订单记录
  - 对应 SKU 库存正确扣减（乐观锁生效）
  - 用户购物车中对应商品被清空
- 下单失败（库存不足等）时，数据库数据保持一致（事务回滚）
- 提供订单列表和详情查询接口
- 所有接口都有良好的异常处理和返回

---

## 7. 后续演进预期（demo023+ 参考）

- **demo023_mall**：订单状态机 + 取消订单 + 简单支付模拟
- **demo024_mall**：引入 Redis 做库存扣减预扣 + 分布式锁
- **demo025_mall**：订单超时自动取消（定时任务或消息队列）

---

## 8. 我的建议

**推荐在 demo022_mall 中重点做好以下三件事**：

1. **把事务这件事情讲清楚、做好**（这是本阶段最大价值）
2. **把 Order + OrderItem 的领域模型设计好**
3. **让下单这个动作真正「可用」**（而不是只做个壳）

是否要我现在就开始：
- 详细设计 `demo022_mall` 的表结构、DTO、Service 接口？
- 或者先创建 `feature/demo022_mall` 分支并开始编码？

请告诉我你的决定。