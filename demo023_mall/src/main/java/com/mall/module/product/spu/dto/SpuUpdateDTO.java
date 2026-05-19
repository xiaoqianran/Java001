package com.mall.module.product.spu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新 SPU 请求
 */
@Data
public class SpuUpdateDTO {

    @NotNull(message = "SPU ID不能为空")
    private Long id;

    private Long categoryId;

    private String name;

    private String description;

    private String brand;

    private Integer status;
}