package com.mall.module.payment.controller;

import com.mall.common.exception.BusinessException;
import com.mall.common.result.Result;
import com.mall.common.security.SecurityUtils;
import com.mall.module.payment.dto.MockPaymentCallbackDTO;
import com.mall.module.payment.service.PaymentService;
import com.mall.module.payment.vo.PaymentOrderVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器（Phase 9）
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 为订单创建支付单
     */
    @PostMapping("/order/{orderId}")
    public Result<PaymentOrderVO> createPayment(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        PaymentOrderVO vo = paymentService.createPaymentForOrder(userId, orderId);
        return Result.success(vo);
    }

    /**
     * 模拟第三方支付回调（不要求登录）
     */
    @PostMapping("/mock-callback")
    public Result<Void> mockCallback(@RequestBody MockPaymentCallbackDTO dto) {
        paymentService.handleMockCallback(dto);
        return Result.success("回调处理成功", null);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        var loginUser = SecurityUtils.getCurrentUser(request);
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new BusinessException(401, "请先登录");
        }
        return loginUser.getUserId();
    }
}
