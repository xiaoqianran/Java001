package com.mall.module.payment.service;

import com.mall.common.security.LoginUser;
import com.mall.module.payment.dto.MockPaymentCallbackDTO;
import com.mall.module.payment.vo.PaymentOrderVO;

/**
 * 支付服务（Phase 9）
 */
public interface PaymentService {

    /**
     * 为订单创建支付单
     */
    PaymentOrderVO createPaymentForOrder(Long userId, Long orderId);

    /**
     * 处理模拟支付回调
     */
    void handleMockCallback(MockPaymentCallbackDTO dto);

    /**
     * 退款已支付订单（Phase 10）
     *
     * 规则：
     * - 必须登录，BUYER 只能退自己的，ADMIN 可退任意，SELLER 可退
     * - 仅订单 status=20 + payment_order status=20 可退
     * - 退款后 order 20→60，payment 20→40，恢复库存
     * - 使用条件更新防并发
     */
    void refundOrder(LoginUser operator, Long orderId);
}
