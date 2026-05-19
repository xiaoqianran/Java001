package com.mall.module.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.module.cart.entity.Cart;
import com.mall.module.cart.vo.CartVO;

import java.util.List;

/**
 * =====================================================================
 * 【demo021_mall - Phase 3 Step 1】购物车 Service
 * =====================================================================
 *
 * 核心能力：
 * - 添加商品到购物车（已存在则累加数量）
 * - 修改数量
 * - 删除购物车商品
 * - 获取当前用户的购物车列表（带 SKU 实时信息）
 *
 * 教学重点：
 * - 购物车通常需要实时带出 SKU 的价格和库存（因为价格可能变动）
 * - 添加时要判断 SKU 是否有效、上架
 * =====================================================================
 */
public interface CartService extends IService<Cart> {

    /**
     * 添加到购物车（已存在则数量累加）
     */
    boolean addToCart(Long userId, Long skuId, Integer quantity);

    /**
     * 修改购物车中某商品的数量
     */
    boolean updateQuantity(Long userId, Long skuId, Integer quantity);

    /**
     * 删除购物车中的商品
     */
    boolean removeFromCart(Long userId, Long skuId);

    /**
     * 获取当前用户的购物车列表（携带 SKU 信息）
     */
    List<CartVO> getUserCart(Long userId);

    /**
     * 【Phase 4 新增】批量删除用户购物车中的指定 SKU
     * 用于下单成功后原子性清空已购买的购物车商品
     */
    boolean removeCartItems(Long userId, List<Long> skuIds);
}