package com.mall.module.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车返回 VO（携带 SKU 实时信息）
 */
@Data
public class CartVO {

    private Long id;           // 购物车ID
    private Long skuId;
    private Integer quantity;

    // 来自 SKU 的实时信息
    private String skuCode;
    private BigDecimal price;
    private Integer stock;
    private String specs;

    // 来自 SPU 的信息（可选）
    private String productName;
}