package com.mall.module.product.category.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.exception.BusinessException;
import com.mall.module.product.category.dto.CategoryUpdateDTO;
import com.mall.module.product.category.entity.Category;
import com.mall.module.product.category.mapper.CategoryMapper;
import com.mall.module.product.category.service.CategoryService;
import com.mall.module.product.category.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 2】商品分类 Service 实现（增强版）
 * =====================================================================
 *
 * 本次重点改进：
 * 1. 使用 Map + 递归 构建真正的树形结构
 * 2. 支持更新和移动分类
 * 3. 删除前置校验（有子节点时禁止删除）
 *
 * 教学重点：
 * - 为什么递归构建树比平铺查询更清晰？
 * - 如何避免 N+1 查询问题（这里用了一次全表查询 + 内存组装）
 * =====================================================================
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 1. 查询所有启用的分类（一次查询，避免 N+1）
        List<Category> allList = lambdaQuery()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();

        if (allList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 转为 VO
        List<CategoryVO> voList = allList.stream().map(this::convertToVO).collect(Collectors.toList());

        // 3. 按 parentId 分组
        Map<Long, List<CategoryVO>> parentMap = voList.stream()
                .collect(Collectors.groupingBy(CategoryVO::getParentId));

        // 4. 找到顶级分类（parentId = 0）
        List<CategoryVO> rootList = parentMap.getOrDefault(0L, new ArrayList<>());

        // 5. 递归构建子节点
        buildTree(rootList, parentMap);

        return rootList;
    }

    /**
     * 递归构建树
     */
    private void buildTree(List<CategoryVO> parentList, Map<Long, List<CategoryVO>> parentMap) {
        for (CategoryVO parent : parentList) {
            List<CategoryVO> children = parentMap.get(parent.getId());
            if (children != null && !children.isEmpty()) {
                parent.setChildren(children);
                buildTree(children, parentMap); // 递归
            }
        }
    }

    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    @Override
    public boolean updateCategory(CategoryUpdateDTO dto) {
        Category category = getById(dto.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        category.setName(dto.getName());
        if (dto.getParentId() != null) {
            category.setParentId(dto.getParentId());
        }
        category.setSort(dto.getSort());
        category.setIcon(dto.getIcon());
        category.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            category.setStatus(dto.getStatus());
        }

        return updateById(category);
    }

    @Override
    public boolean moveCategory(Long id, Long newParentId) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 简单校验：不能将自己移动到自己的子节点下（更严格的校验可后续加强）
        if (id.equals(newParentId)) {
            throw new BusinessException("不能将分类移动到自己下面");
        }

        category.setParentId(newParentId);
        return updateById(category);
    }

    @Override
    public boolean safeDelete(Long id) {
        // 检查是否有子分类
        long childrenCount = lambdaQuery()
                .eq(Category::getParentId, id)
                .count();

        if (childrenCount > 0) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }

        // TODO: 未来可增加“是否有商品”校验
        return removeById(id);
    }
}