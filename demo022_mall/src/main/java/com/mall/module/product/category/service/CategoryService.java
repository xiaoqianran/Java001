package com.mall.module.product.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.module.product.category.dto.CategoryCreateDTO;
import com.mall.module.product.category.dto.CategoryUpdateDTO;
import com.mall.module.product.category.entity.Category;
import com.mall.module.product.category.vo.CategoryVO;

import java.util.List;

/**
 * =====================================================================
 * 【demo021_mall - Phase 2 Step 2】商品分类 Service（增强版）
 * =====================================================================
 *
 * 本步骤重点改进：
 * - 提供真正的树形结构数据（带 children）
 * - 支持分类更新和移动
 * - 增加删除前置校验（有子分类时禁止删除）
 *
 * 教学重点：
 * - 递归构建树 vs 循环构建树
 * - 业务规则的封装（删除保护）
 * =====================================================================
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取完整的分类树（支持无限层级）
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 更新分类信息
     */
    boolean updateCategory(CategoryUpdateDTO dto);

    /**
     * 移动分类（修改父分类）
     */
    boolean moveCategory(Long id, Long newParentId);

    /**
     * 安全删除分类（有子分类时不允许删除）
     */
    boolean safeDelete(Long id);

    /**
     * 创建分类（推荐方式，已将映射逻辑从 Controller 下沉）
     */
    boolean createCategory(CategoryCreateDTO dto);
}