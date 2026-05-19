package com.mall.module.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求 DTO（Phase 4 - demo022_mall）
 */
@Data
public class OrderCreateDTO {

    @NotEmpty(message = "订单商品不能为空")
    @Valid
    private List<OrderItemDTO> items;
}