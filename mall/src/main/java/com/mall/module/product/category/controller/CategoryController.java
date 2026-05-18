package com.mall.module.product.category.controller;

import com.mall.common.result.Result;
import com.mall.module.product.category.dto.CategoryCreateDTO;
import com.mall.module.product.category.dto.CategoryUpdateDTO;
import com.mall.module.product.category.service.CategoryService;
import com.mall.module.product.category.vo.CategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 2】商品分类 Controller（增强版）
 * =====================================================================
 *
 * 本步骤主要改进：
 * - 返回真正的树形结构（CategoryVO 带 children）
 * - 支持更新和移动分类
 * - 安全删除（有子分类时禁止）
 *
 * 教学重点：
 * - Controller 层应该尽量薄，复杂逻辑下沉到 Service
 * - 树形接口的设计思路
 * =====================================================================
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取完整的分类树（推荐前端使用）
     */
    @GetMapping("/tree")
    public Result<List<CategoryVO>> getTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Boolean> create(@Valid @RequestBody CategoryCreateDTO dto) {
        // 简化创建逻辑，实际可移到 Service
        com.mall.module.product.category.entity.Category category = new com.mall.module.product.category.entity.Category();
        category.setParentId(dto.getParentId());
        category.setName(dto.getName());
        category.setLevel(dto.getLevel());
        category.setSort(dto.getSort());
        category.setIcon(dto.getIcon());
        category.setDescription(dto.getDescription());
        category.setStatus(1);

        boolean success = categoryService.save(category);
        return success ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新分类
     */
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody CategoryUpdateDTO dto) {
        boolean success = categoryService.updateCategory(dto);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 移动分类（修改父分类）
     */
    @PutMapping("/{id}/move")
    public Result<Boolean> move(@PathVariable Long id, @RequestParam Long newParentId) {
        boolean success = categoryService.moveCategory(id, newParentId);
        return success ? Result.success(true) : Result.error("移动失败");
    }

    /**
     * 安全删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = categoryService.safeDelete(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
}