package com.mall.module.order.service;

import com.mall.module.order.dto.OrderCreateDTO;

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
}