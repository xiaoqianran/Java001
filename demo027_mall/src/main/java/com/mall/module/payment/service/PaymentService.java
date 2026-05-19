package com.mall.module.payment.service;

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
}
