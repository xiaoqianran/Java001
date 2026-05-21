package com.mall.module.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单商品项 DTO
 */
@Data
public class OrderItemDTO {

    @NotNull(message = "SKU ID 不能为空")
    private Long skuId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;
}