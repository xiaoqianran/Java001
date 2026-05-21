package com.mall.module.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项 VO
 */
@Data
public class OrderItemVO {

    private Long id;
    private Long skuId;
    private String skuName;
    private String skuSpecs;
    private BigDecimal price;
    private Integer quantity;
}