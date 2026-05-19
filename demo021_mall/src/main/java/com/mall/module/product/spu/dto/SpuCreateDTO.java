package com.mall.module.product.spu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建 SPU 请求
 */
@Data
public class SpuCreateDTO {

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String description;

    private String brand;
}