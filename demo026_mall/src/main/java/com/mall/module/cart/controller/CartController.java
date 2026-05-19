package com.mall.module.cart.controller;

import com.mall.common.result.Result;
import com.mall.common.security.SecurityUtils;
import com.mall.module.cart.dto.CartAddDTO;
import com.mall.module.cart.dto.CartUpdateDTO;
import com.mall.module.cart.service.CartService;
import com.mall.module.cart.vo.CartVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【demo021_mall - Phase 3 Step 1 收尾修复】购物车 Controller
 * =====================================================================
 *
 * 购物车是用户从“看”到“买”的第一步。
 *
 * 本步骤教学目标：
 * - 实现购物车最基础的增删改查
 * - 展示如何在 Service 层关联 SKU 实时信息
 * - 为后续“生成订单”做数据准备
 *
 * 注意：当前购物车只支持已登录用户，后续可扩展“未登录购物车 + 登录合并”。
 * =====================================================================
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取当前登录用户的购物车列表
     */
    @GetMapping
    public Result<List<CartVO>> getCart(HttpServletRequest request) {
        // 【修复】统一使用 SecurityUtils 获取当前用户（与 AuthController 保持一致）
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        return Result.success(cartService.getUserCart(userId));
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    public Result<Boolean> addToCart(@Valid @RequestBody CartAddDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        boolean success = cartService.addToCart(userId, dto.getSkuId(), dto.getQuantity());
        return success ? Result.success(true) : Result.error("添加失败");
    }

    /**
     * 修改购物车中商品数量
     */
    @PutMapping
    public Result<Boolean> updateQuantity(@Valid @RequestBody CartUpdateDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        boolean success = cartService.updateQuantity(userId, dto.getSkuId(), dto.getQuantity());
        return success ? Result.success(true) : Result.error("修改失败");
    }

    /**
     * 删除购物车中的商品
     */
    @DeleteMapping("/{skuId}")
    public Result<Boolean> removeFromCart(@PathVariable Long skuId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        boolean success = cartService.removeFromCart(userId, skuId);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 私有辅助方法：统一通过 SecurityUtils 获取当前登录用户ID
     * 这是推荐的做法，Controller 层也应避免直接操作 request attribute
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        var loginUser = SecurityUtils.getCurrentUser(request);
        return loginUser != null ? loginUser.getUserId() : null;
    }
}