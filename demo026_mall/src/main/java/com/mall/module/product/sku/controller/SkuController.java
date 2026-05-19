package com.mall.module.product.sku.controller;

import com.mall.common.result.Result;
import com.mall.module.product.sku.dto.SkuCreateDTO;
import com.mall.module.product.sku.entity.Sku;
import com.mall.module.product.sku.service.SkuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 4】SKU Controller
 * =====================================================================
 *
 * SKU 是电商系统中**真正进行交易的最小单位**。
 *
 * 本步骤教学目标：
 * - 建立 SPU 与 SKU 的一对多关系
 * - 实现 SKU 的基础管理
 * - 为后续“下单扣库存”做准备
 *
 * 注意：当前库存扣减逻辑比较简单，放在订单模块时会进一步加强事务和并发控制。
 * =====================================================================
 */
@RestController
@RequestMapping("/api/sku")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    /**
     * 根据 SPU 查询所有 SKU
     */
    @GetMapping("/spu/{spuId}")
    public Result<List<Sku>> listBySpu(@PathVariable Long spuId) {
        return Result.success(skuService.listBySpuId(spuId));
    }

    /**
     * 创建 SKU
     */
    @PostMapping
    public Result<Boolean> create(@Valid @RequestBody SkuCreateDTO dto) {
        Sku sku = new Sku();
        sku.setSpuId(dto.getSpuId());
        sku.setSkuCode(dto.getSkuCode());
        sku.setPrice(dto.getPrice());
        sku.setStock(dto.getStock());
        sku.setSpecs(dto.getSpecs());
        sku.setStatus(1);

        boolean success = skuService.save(sku);
        return success ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 扣减库存（测试接口，实际应在订单服务调用）
     * 使用乐观锁保护
     */
    @PostMapping("/{id}/reduce")
    public Result<Boolean> reduceStock(@PathVariable Long id, @RequestParam Integer quantity) {
        boolean success = skuService.reduceStock(id, quantity);
        return success ? Result.success(true) : Result.error("扣减失败");
    }

    /**
     * 删除 SKU
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = skuService.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
}