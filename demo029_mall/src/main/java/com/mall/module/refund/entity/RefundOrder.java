package com.mall.module.refund.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 退款申请单实体（Phase 11）
 *
 * 对应表：refund_order
 * 核心字段：
 * - 关联订单 + 申请人
 * - 申请原因
 * - 状态：10 待审核 / 20 已通过 / 30 已拒绝
 * - 审核信息（审核人、时间、备注）
 */
@Data
@TableName("refund_order")
public class RefundOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联订单ID */
    private Long orderId;

    /** 申请人用户ID（BUYER） */
    private Long userId;

    /** 退款原因 */
    private String reason;

    /**
     * 退款申请状态
     * 10 = 待审核（买家刚提交）
     * 20 = 已通过（审核通过，已执行退款）
     * 30 = 已拒绝（审核拒绝）
     */
    private Integer status;

    /** 申请时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime applyTime;

    /** 审核时间 */
    private LocalDateTime reviewTime;

    /** 审核人用户ID（ADMIN 或 SELLER） */
    private Long reviewerId;

    /** 审核备注（通过说明或拒绝原因） */
    private String reviewRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
