package com.mall.module.product.spu.controller;

import com.mall.common.result.Result;
import com.mall.module.product.spu.dto.SpuCreateDTO;
import com.mall.module.product.spu.dto.SpuUpdateDTO;
import com.mall.module.product.spu.entity.Spu;
import com.mall.module.product.spu.service.SpuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 3】SPU Controller
 * =====================================================================
 *
 * 这是 mall 项目商品域的**核心起点**。
 *
 * 本步骤教学目标：
 * - 引入 SPU 概念（标准产品单元）
 * - 理解 SPU 与分类、SKU 的关系
 * - 实现商品基本信息的增删改查
 *
 * 注意：当前 SPU 还不包含 SKU，下一阶段会叠加规格和库存。
 * =====================================================================
 */
@RestController
@RequestMapping("/api/spu")
@RequiredArgsConstructor
public class SpuController {

    private final SpuService spuService;

    /**
     * 根据分类查询已上架的 SPU 列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Spu>> listByCategory(@PathVariable Long categoryId) {
        return Result.success(spuService.listByCategory(categoryId));
    }

    /**
     * 创建 SPU（商品）
     */
    @PostMapping
    public Result<Boolean> create(@Valid @RequestBody SpuCreateDTO dto) {
        Spu spu = new Spu();
        spu.setCategoryId(dto.getCategoryId());
        spu.setName(dto.getName());
        spu.setDescription(dto.getDescription());
        spu.setBrand(dto.getBrand());
        spu.setStatus(1); // 默认上架

        boolean success = spuService.save(spu);
        return success ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新 SPU
     */
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody SpuUpdateDTO dto) {
        Spu spu = spuService.getById(dto.getId());
        if (spu == null) {
            return Result.error("商品不存在");
        }

        if (dto.getCategoryId() != null) spu.setCategoryId(dto.getCategoryId());
        if (dto.getName() != null) spu.setName(dto.getName());
        if (dto.getDescription() != null) spu.setDescription(dto.getDescription());
        if (dto.getBrand() != null) spu.setBrand(dto.getBrand());
        if (dto.getStatus() != null) spu.setStatus(dto.getStatus());

        boolean success = spuService.updateById(spu);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 上架 / 下架
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = spuService.updateStatus(id, status);
        return success ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 删除 SPU（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = spuService.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
}