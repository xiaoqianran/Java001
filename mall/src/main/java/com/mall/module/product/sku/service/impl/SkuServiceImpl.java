package com.mall.module.product.sku.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.exception.BusinessException;
import com.mall.module.product.sku.entity.Sku;
import com.mall.module.product.sku.mapper.SkuMapper;
import com.mall.module.product.sku.service.SkuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 4】SKU Service 实现
 * =====================================================================
 *
 * 教学重点：
 * - 库存扣减必须加事务（@Transactional）
 * - 简单乐观锁思路（当前用 stock >= quantity 校验）
 * - 后续可升级为数据库行锁或 Redis 分布式锁
 * =====================================================================
 */
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {

    @Override
    public List<Sku> listBySpuId(Long spuId) {
        return lambdaQuery()
                .eq(Sku::getSpuId, spuId)
                .eq(Sku::getStatus, 1)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long skuId, Integer quantity) {
        Sku sku = getById(skuId);
        if (sku == null || sku.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        sku.setStock(sku.getStock() - quantity);
        return updateById(sku);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStock(Long skuId, Integer quantity) {
        Sku sku = getById(skuId);
        if (sku == null) {
            throw new BusinessException("SKU 不存在");
        }

        sku.setStock(sku.getStock() + quantity);
        return updateById(sku);
    }
}