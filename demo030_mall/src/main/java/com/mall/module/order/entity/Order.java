package com.mall.module.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体（Phase 4 - demo022_mall）
 *
 * 设计要点：
 * - 使用 @TableName("`order`") 因为 order 是 SQL 关键字
 * - status 使用 10、20、30... 便于后续扩展状态
 * - 保留 deleted、create_time、update_time 保持与项目一致
 */
@Data
@TableName("`order`")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务订单编号（例如 ORD202605191230001234） */
    private String orderNo;

    /** 下单用户ID */
    private Long userId;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /**
     * 订单状态：
     * 10=待支付, 20=已支付, 30=已发货, 40=已完成, 50=已取消
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}