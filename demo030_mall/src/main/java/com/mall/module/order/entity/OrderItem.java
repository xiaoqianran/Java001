package com.mall.module.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体（Phase 4 - demo022_mall）
 *
 * 设计要点：
 * - 使用快照字段（skuName、skuSpecs、price）保护历史订单数据
 * - 不使用逻辑删除（订单项跟随订单主表生命周期）
 */
@Data
@TableName("order_item")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属订单ID */
    private Long orderId;

    /** SKU ID */
    private Long skuId;

    /** 商品名称快照（防止商品信息后续修改） */
    private String skuName;

    /** 规格快照（JSON 字符串） */
    private String skuSpecs;

    /** 下单时单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer quantity;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}