package com.mall.module.product.sku.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建 SKU 请求
 */
@Data
public class SkuCreateDTO {

    @NotNull(message = "SPU ID不能为空")
    private Long spuId;

    @NotBlank(message = "SKU 编码不能为空")
    private String skuCode;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    /**
     * 规格 JSON 字符串，例如：{"颜色":"黑色","内存":"128G"}
     */
    private String specs;
}