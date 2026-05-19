package com.mall.module.product.category.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 2】商品分类树形 VO
 * =====================================================================
 *
 * 教学重点：
 * - 为什么需要单独的 VO 而不是直接返回 Entity？
 * - 如何设计支持无限层级的树形结构
 * - 前后端分离中，VO 的作用是“按需返回数据”
 *
 * 这个 VO 专门用于返回分类树，包含 children 子节点列表。
 * =====================================================================
 */
@Data
public class CategoryVO {

    private Long id;
    private Long parentId;
    private String name;
    private Integer level;
    private Integer sort;
    private String icon;
    private String description;
    private Integer status;

    /** 子分类列表 */
    private List<CategoryVO> children = new ArrayList<>();
}