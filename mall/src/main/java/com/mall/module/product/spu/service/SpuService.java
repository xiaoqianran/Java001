package com.mall.module.product.spu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.module.product.spu.entity.Spu;

import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 3】SPU Service 接口
 * =====================================================================
 *
 * SPU 的核心业务能力：
 * - 基础 CRUD
 * - 按分类查询商品
 * - 上架/下架操作
 *
 * 注意：当前阶段 SPU 还比较简单，重点是理解概念。
 * 后续会关联 SKU、属性、图片等多维度信息。
 * =====================================================================
 */
public interface SpuService extends IService<Spu> {

    /**
     * 根据分类查询 SPU 列表
     */
    List<Spu> listByCategory(Long categoryId);

    /**
     * 上架 / 下架商品
     */
    boolean updateStatus(Long id, Integer status);
}