package com.mall.module.product.category.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.module.product.category.entity.Category;
import com.mall.module.product.category.mapper.CategoryMapper;
import com.mall.module.product.category.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 1】商品分类 Service 实现
 * =====================================================================
 *
 * 重点教学内容：
 * - 如何使用 Stream + 递归（或循环）构建树形结构
 * - 分类查询的常见实现方式
 * =====================================================================
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoryTree() {
        // 查询所有启用的分类
        List<Category> allCategories = lambdaQuery()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();

        // 构建树形结构（简单实现：只返回一级 + 二级示例）
        return allCategories.stream()
                .filter(c -> c.getParentId() == 0)
                .peek(parent -> {
                    List<Category> children = allCategories.stream()
                            .filter(c -> c.getParentId().equals(parent.getId()))
                            .collect(Collectors.toList());
                    // 这里可以把 children 存到扩展字段，当前简单返回平铺数据
                })
                .collect(Collectors.toList());
    }
}