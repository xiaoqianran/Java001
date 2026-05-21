package com.mall.module.refund.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 退款申请 VO（Phase 11）
 */
@Data
public class RefundVO {

    private Long id;
    private Long orderId;
    private Long userId;
    private String reason;
    private java.math.BigDecimal refundAmount;  // Phase 12 新增
    private Integer status;
    private String statusDesc;   // 10-待审核 etc.
    private LocalDateTime applyTime;
    private LocalDateTime reviewTime;
    private Long reviewerId;
    private String reviewRemark;
}
