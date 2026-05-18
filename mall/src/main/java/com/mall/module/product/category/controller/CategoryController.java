package com.mall.module.product.category.controller;

import com.mall.common.result.Result;
import com.mall.module.product.category.dto.CategoryCreateDTO;
import com.mall.module.product.category.entity.Category;
import com.mall.module.product.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 1】商品分类 Controller
 * =====================================================================
 *
 * 这是 mall 项目进入真正业务领域的第一个 Controller。
 *
 * 本步骤教学目标：
 * - 建立商品分类的基础增删改查能力
 * - 演示分类树查询接口
 * - 为后续 SPU/SKU 模块打下基础
 * =====================================================================
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<Category>> getTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Boolean> create(@Valid @RequestBody CategoryCreateDTO dto) {
        Category category = new Category();
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
     * 删除分类（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = categoryService.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
}