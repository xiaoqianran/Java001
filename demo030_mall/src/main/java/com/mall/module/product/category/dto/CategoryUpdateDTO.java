package com.mall.module.product.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新分类请求 DTO
 */
@Data
public class CategoryUpdateDTO {

    @NotNull(message = "分类ID不能为空")
    private Long id;

    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer sort;

    private String icon;

    private String description;

    private Integer status;
}