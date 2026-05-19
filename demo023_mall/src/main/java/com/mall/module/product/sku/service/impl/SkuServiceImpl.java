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
 * 【demo021_mall - Phase 2 Step 4 完善】SKU 库存管理实现（引入乐观锁）
 * =====================================================================
 *
 * 本次核心改进：
 * - 使用 MyBatis-Plus 的 @Version 实现乐观锁
 * - 防止高并发下库存超卖问题
 *
 * 教学重点：
 * - 为什么简单的 `stock = stock - n` 在并发场景下危险？
 * - 乐观锁的工作原理（版本号对比）
 * - MyBatis-Plus 如何自动处理 version 字段
 *
 * 注意：当前实现仍属于“应用层乐观锁”，生产环境高并发时建议结合 Redis + Lua 或数据库行锁。
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
    public boolean reduceStock(Long skuId, Integer quantity) {
        Sku sku = getById(skuId);
        if (sku == null) {
            throw new BusinessException("SKU 不存在");
        }
        if (sku.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        // 使用乐观锁更新（MyBatis-Plus 会自动根据 version 条件更新）
        sku.setStock(sku.getStock() - quantity);

        // updateById 会自动带上 version 条件：WHERE id = ? AND version = ?
        boolean success = updateById(sku);

        if (!success) {
            // 说明 version 不匹配，即并发冲突
            throw new BusinessException("库存扣减失败，请重试");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreStock(Long skuId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("恢复库存数量必须大于0");
        }

        Sku sku = getById(skuId);
        if (sku == null) {
            throw new BusinessException("SKU 不存在");
        }

        // 乐观锁回补：getById 已带 version，updateById 自动 WHERE version = ?
        sku.setStock(sku.getStock() + quantity);
        boolean success = updateById(sku);
        if (!success) {
            // 必须抛出确切消息，便于上层事务回滚
            throw new BusinessException("库存回滚失败，请重试");
        }
        return true;
    }
}