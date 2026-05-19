package com.mall.module.product.category.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 1】商品分类实体（Category）
 * =====================================================================
 *
 * 本步骤核心变化：
 * - 引入商品分类模块（电商系统的第一块业务领域）
 * - 支持树形结构（parent_id + level）
 *
 * 教学重点：
 * - 电商系统中分类的常见设计（树形 + 层级 + 排序）
 * - MyBatis-Plus 逻辑删除 + 自动填充时间
 * - 为什么不一开始就设计非常复杂的分类模型？
 *
 * 后续演进：
 * - 支持多级无限分类
 * - 增加分类属性（用于 SKU 筛选）
 * =====================================================================
 */
@Data
@TableName("product_category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父分类ID，0表示顶级 */
    private Long parentId;

    private String name;

    private Integer level;

    private Integer sort;

    private String icon;

    private String description;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}