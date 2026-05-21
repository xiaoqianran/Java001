package com.mall.module.refund.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 退款申请请求 DTO（Phase 11）
 */
@Data
public class RefundApplyDTO {

    @NotBlank(message = "退款原因不能为空")
    @Size(max = 255, message = "退款原因最多255字符")
    private String reason;
}
