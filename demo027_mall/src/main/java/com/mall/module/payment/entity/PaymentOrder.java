package com.mall.module.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付单实体（Phase 9）
 */
@Data
@TableName("payment_order")
public class PaymentOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 支付单号 */
    private String paymentNo;

    /** 订单ID */
    private Long orderId;

    /** 订单号快照 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付渠道 */
    private String channel;

    /** 支付状态 10=待支付, 20=支付成功, 30=支付失败 */
    private Integer status;

    /** 回调时间 */
    private LocalDateTime callbackTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
