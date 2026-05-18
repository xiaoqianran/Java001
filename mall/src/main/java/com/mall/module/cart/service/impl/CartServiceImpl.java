package com.mall.module.cart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.exception.BusinessException;
import com.mall.module.cart.entity.Cart;
import com.mall.module.cart.mapper.CartMapper;
import com.mall.module.cart.service.CartService;
import com.mall.module.cart.vo.CartVO;
import com.mall.module.product.sku.entity.Sku;
import com.mall.module.product.sku.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * =====================================================================
 * 【mall - Phase 3 Step 1】购物车 Service 实现
 * =====================================================================
 *
 * 实现要点：
 * - 添加商品时判断是否已存在，存在则累加数量
 * - 查询购物车时关联 SKU 获取最新价格和库存
 * - 所有操作都基于 userId + skuId 唯一性
 *
 * 教学价值：
 * - 展示如何在 Service 层做业务规则判断
 * - 展示关联查询的常用写法（先查 cart，再批量查 sku）
 * =====================================================================
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final SkuService skuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToCart(Long userId, Long skuId, Integer quantity) {
        // 1. 检查 SKU 是否有效
        Sku sku = skuService.getById(skuId);
        if (sku == null || sku.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }

        // 2. 查找用户是否已有该 SKU
        Cart existCart = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getSkuId, skuId)
                .one();

        if (existCart != null) {
            // 已存在 → 累加数量
            existCart.setQuantity(existCart.getQuantity() + quantity);
            return updateById(existCart);
        } else {
            // 不存在 → 新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setSkuId(skuId);
            cart.setQuantity(quantity);
            return save(cart);
        }
    }

    @Override
    public boolean updateQuantity(Long userId, Long skuId, Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException("数量必须大于0");
        }

        return lambdaUpdate()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getSkuId, skuId)
                .set(Cart::getQuantity, quantity)
                .update();
    }

    @Override
    public boolean removeFromCart(Long userId, Long skuId) {
        return lambdaUpdate()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getSkuId, skuId)
                .remove();
    }

    @Override
    public List<CartVO> getUserCart(Long userId) {
        // 1. 查询用户购物车
        List<Cart> cartList = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .list();

        if (cartList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 批量查询 SKU 信息
        List<Long> skuIds = cartList.stream().map(Cart::getSkuId).collect(Collectors.toList());
        List<Sku> skuList = skuService.listByIds(skuIds);
        Map<Long, Sku> skuMap = skuList.stream().collect(Collectors.toMap(Sku::getId, s -> s));

        // 3. 组装返回结果
        return cartList.stream().map(cart -> {
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setSkuId(cart.getSkuId());
            vo.setQuantity(cart.getQuantity());

            Sku sku = skuMap.get(cart.getSkuId());
            if (sku != null) {
                vo.setSkuCode(sku.getSkuCode());
                vo.setPrice(sku.getPrice());
                vo.setStock(sku.getStock());
                vo.setSpecs(sku.getSpecs());
                // TODO: 后续可关联 SPU 获取商品名称
            }
            return vo;
        }).collect(Collectors.toList());
    }
}