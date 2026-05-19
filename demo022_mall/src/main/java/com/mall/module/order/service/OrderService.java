package com.mall.module.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.module.order.dto.OrderCreateDTO;
import com.mall.module.order.vo.OrderVO;

/**
 * 订单服务接口（Phase 4 - demo022_mall）
 *
 * 核心目标：
 * - 实现下单事务（创建订单 + 扣减库存 + 清空购物车）
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
}