package com.mall.module.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建支付单请求（Phase 9）
 */
@Data
public class PaymentCreateDTO {
    // 目前主要通过路径参数 orderId 创建，此 DTO 可预留扩展
}
