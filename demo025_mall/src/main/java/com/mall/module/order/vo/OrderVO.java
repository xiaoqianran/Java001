package com.mall.module.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单返回 VO（Phase 4 - demo022_mall）
 */
@Data
public class OrderVO {

    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createTime;

    /** 订单明细 */
    private List<OrderItemVO> items;
}