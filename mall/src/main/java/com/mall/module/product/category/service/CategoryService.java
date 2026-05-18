package com.mall.module.product.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.module.product.category.entity.Category;

import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 1】商品分类 Service
 * =====================================================================
 *
 * 提供分类的业务能力：
 * - 基础 CRUD（继承 IService）
 * - 树形结构查询（getCategoryTree）
 *
 * 教学价值：
 * - 如何在 Service 层封装树形数据组装逻辑
 * =====================================================================
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树（用于前端展示）
     */
    List<Category> getCategoryTree();
}