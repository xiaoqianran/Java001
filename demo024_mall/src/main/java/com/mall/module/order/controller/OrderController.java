package com.mall.module.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.exception.BusinessException;
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
 * 订单控制器（Phase 4/5 - demo023_mall）
 * Phase 5 新增：取消订单接口 + 状态机流转控制（逻辑不散落在 Controller）
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

    /**
     * 取消订单（Phase 5）
     *
     * 安全与业务规则：
     * - 必须登录（getCurrentUserId 保证 401）
     * - 只能取消自己的订单（service 内 403）
     * - 仅 status=10 待支付可取消（状态机判断）
     * - 成功后状态=50 + 库存回滚（同一事务）
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        orderService.cancelOrder(userId, id);
        return Result.success("取消成功", null);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        var loginUser = SecurityUtils.getCurrentUser(request);
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new BusinessException(401, "请先登录");
        }
        return loginUser.getUserId();
    }
}