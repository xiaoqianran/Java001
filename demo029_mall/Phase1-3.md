# demo021_mall Phase 1-3 历史基线文档（demo023_mall 继承）

> **本文件是历史基线文档**  
> - demo021_mall 完成了 Phase 1-3：用户认证 + 商品域 + 购物车  
> - demo022_mall 在此基础上实现 **Phase 4：订单基础闭环**（下单事务 + 库存扣减 + 清购物车）  
> - demo023_mall 在此基础上实现 **Phase 5：订单状态机与取消订单**（仅待支付可取消 + 库存回滚）  
>  
> demo023_mall 完整继承 demo021_mall 的 Phase 1-3 能力。本文档仅作为历史参考保留，不再维护。

---

## Phase 1：用户与认证体系

- 用户注册（唯一性校验 + BCrypt 加密）
- JWT 登录 + 无状态认证
- Spring Security + RBAC（ADMIN / SELLER / BUYER）
- `@PreAuthorize` 方法级权限控制

---

## Phase 2：商品域（Category + SPU + SKU）

- Category：三级分类、树形查询、移动、逻辑删除保护
- SPU：标准产品单元（基本信息）
- SKU：规格、价格、库存（乐观锁 `version` 防超卖）
- 教学重点：领域建模 + 乐观锁实战

---

## Phase 3：购物车

- 添加（数量累加）、修改数量、删除、列表
- 实时关联 SKU 当前价格/库存/规格
- 用户态购物车（仅本人可见）

---

## 已知历史技术债（供后续版本参考）

- 缺少通用分页/排序/条件查询封装（当时留待 demo023+ 解决）
- 部分 Controller 直接操作 Mapper
- 缺少商品详情聚合接口（SPU+SKU 一起返回）

---

## 后续演进关系

```
demo021_mall (Phase 1-3)
       ↓
demo022_mall (Phase 4：订单基础闭环)
       ↓
demo023_mall (Phase 5：状态机 + 取消订单)
```

**本文件为 demo023_mall 的历史基线文档，内容不再更新。**
