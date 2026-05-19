package com.mall.module.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.security.LoginUser;
import com.mall.module.order.dto.OrderCreateDTO;
import com.mall.module.order.vo.OrderVO;

/**
 * 订单服务接口（Phase 4/5 - demo023_mall 继承 Phase 4 + 状态机）
 *
 * 核心目标：
 * - 实现下单事务（创建订单 + 扣减库存 + 清空购物车）
 * - 实现取消订单 + 状态流转控制 + 库存回滚事务
 * - 保证数据一致性
 */
public interface OrderService {

    /**
     * 创建订单（核心方法）
     *
     * @param userId 当前登录用户ID
     * @param dto    下单商品列表
     * @return 新生成的订单ID
     */
    Long createOrder(Long userId, OrderCreateDTO dto);

    /**
     * 分页查询当前用户的订单列表
     */
    Page<OrderVO> listUserOrders(Long userId, int current, int size);

    /**
     * 查询订单详情（带权限校验）
     */
    OrderVO getOrderDetail(Long userId, Long orderId);

    /**
     * 取消订单（Phase 5 状态机能力）
     *
     * 规则：
     * - 仅 status=10 待支付 可取消
     * - 必须是本人订单
     * - 取消成功：状态 -> 50 + 恢复 order_item 中每个 SKU 的库存
     * - 整个操作在同一个 @Transactional 中，失败全部回滚
     *
     * @param userId 当前登录用户
     * @param orderId 订单ID
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 模拟支付订单（Phase 6）
     *
     * 规则：
     * - 仅 status=10 待支付 可支付
     * - 必须是本人订单
     * - 支付成功后状态 10 → 20
     * - 使用原子条件更新防止并发重复支付
     * - 整个操作在同一个 @Transactional 中
     */
    void payOrder(Long userId, Long orderId);

    /**
     * 发货订单（Phase 7）
     *
     * 规则：
     * - 仅 ADMIN(1) 或 SELLER(2) 可操作
     * - BUYER(3) 调用必须被拒绝
     * - 仅 status=20 已支付 可发货
     * - 成功后状态 20 → 30
     * - 使用原子条件更新防止并发
     */
    void shipOrder(LoginUser operator, Long orderId);

    /**
     * 完成订单（Phase 7）
     *
     * 规则：
     * - ADMIN(1) 可完成任意已发货订单
     * - SELLER(2) 可完成已发货订单
     * - BUYER(3) 只能完成自己的已发货订单
     * - 仅 status=30 已发货 可完成
     * - 成功后状态 30 → 40
     * - 使用原子条件更新防止并发
     */
    void completeOrder(LoginUser operator, Long orderId);

    /**
     * 取消所有超时的待支付订单（Phase 8 定时任务调用）
     *
     * @return 本次成功取消的订单数量
     */
    int cancelTimeoutOrders();
}