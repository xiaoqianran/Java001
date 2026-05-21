package com.mall.module.payment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付单返回 VO（Phase 9）
 */
@Data
public class PaymentOrderVO {

    private Long id;
    private String paymentNo;
    private Long orderId;
    private String orderNo;
    private BigDecimal amount;
    private BigDecimal refundedAmount;  // Phase 12 新增
    private String channel;
    private Integer status;
    private LocalDateTime callbackTime;
    private LocalDateTime createTime;
}
