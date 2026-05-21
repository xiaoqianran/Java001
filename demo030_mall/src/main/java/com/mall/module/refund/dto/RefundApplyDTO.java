package com.mall.module.refund.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 退款申请请求 DTO（Phase 11）
 */
@Data
public class RefundApplyDTO {

    @NotBlank(message = "退款原因不能为空")
    @Size(max = 255, message = "退款原因最多255字符")
    private String reason;

    /**
     * 申请退款金额（Phase 12 新增）
     * 必填 > 0，最多两位小数，且不能超过支付金额（在 Service 层二次校验）
     */
    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0.01", message = "退款金额必须大于0")
    @Digits(integer = 8, fraction = 2, message = "退款金额最多两位小数")
    private BigDecimal refundAmount;
}
