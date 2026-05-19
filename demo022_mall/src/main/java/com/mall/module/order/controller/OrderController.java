package com.mall.module.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.result.Result;
import com.mall.common.security.SecurityUtils;
import com.mall.module.order.dto.OrderCreateDTO;
import com.mall.module.order.service.OrderService;
import com.mall.module.order.vo.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器（Phase 4 - demo022_mall）
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Long> createOrder(@Valid @RequestBody OrderCreateDTO dto,
                                    HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Long orderId = orderService.createOrder(userId, dto);
        return Result.success("下单成功", orderId);
    }

    /**
     * 我的订单列表（分页）
     */
    @GetMapping
    public Result<Page<OrderVO>> listOrders(@RequestParam(defaultValue = "1") int current,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Page<OrderVO> page = orderService.listUserOrders(userId, current, size);
        return Result.success(page);
    }

    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderVO> getDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        OrderVO vo = orderService.getOrderDetail(userId, id);
        return Result.success(vo);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        var loginUser = SecurityUtils.getCurrentUser(request);
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new RuntimeException("请先登录");
        }
        return loginUser.getUserId();
    }
}