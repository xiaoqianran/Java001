package com.mall.module.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 模拟支付回调请求（Phase 9）
 */
@Data
public class MockPaymentCallbackDTO {

    /** 支付单号 */
    private String paymentNo;

    /** 订单ID */
    private Long orderId;

    /** 支付金额 */
    private BigDecimal paidAmount;

    /** 支付结果 SUCCESS / FAILED */
    private String payStatus;

    /** 模拟签名 */
    private String mockSign;
}
