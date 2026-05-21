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
     * 退款已支付订单（Phase 10 历史接口，Phase 11 起仅 ADMIN/SELLER 或内部审核流程调用）
     * 默认全额退款。
     */
    void refundOrder(LoginUser operator, Long orderId);

    /**
     * 按指定金额退款（Phase 12 新增）
     *
     * @param refundAmount 申请退款金额（已在前置校验通过）
     *
     * 规则：
     * - 仅 ADMIN/SELLER 或 RefundService.approveRefund 内部调用
     * - refundAmount > 0 且 <= payment_order.amount
     * - 更新 payment_order.refunded_amount
     * - 无论全额还是部分，本阶段均将订单置为 60（教学简化：一订单一申请）
     */
    void refundOrder(LoginUser operator, Long orderId, java.math.BigDecimal refundAmount);
}
