package com.mall.module.product.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建分类请求
 */
@Data
public class CategoryCreateDTO {

    @NotNull(message = "父分类ID不能为空")
    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer level = 1;

    private Integer sort = 0;

    private String icon;

    private String description;
}