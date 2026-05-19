package com.mall.module.product.sku.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.module.product.sku.entity.Sku;

import java.util.List;

/**
 * =====================================================================
 * 【demo021_mall - Phase 2 Step 4】SKU Service
 * =====================================================================
 *
 * SKU 的核心能力：
 * - 根据 SPU 查询所有 SKU
 * - 扣减库存 / 回补库存（事务关键）
 * - 基础 CRUD
 *
 * 注意：库存操作是后续订单模块的重点，这里先打基础。
 * =====================================================================
 */
public interface SkuService extends IService<Sku> {

    /**
     * 根据 SPU ID 查询所有 SKU
     */
    List<Sku> listBySpuId(Long spuId);

    /**
     * 安全扣减库存（使用乐观锁）
     * 推荐在订单模块调用此方法
     */
    boolean reduceStock(Long skuId, Integer quantity);

    /**
     * 回补库存（用于取消订单等场景）
     */
    boolean restoreStock(Long skuId, Integer quantity);
}