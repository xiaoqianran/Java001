package com.mall.module.product.spu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.module.product.spu.entity.Spu;
import com.mall.module.product.spu.mapper.SpuMapper;
import com.mall.module.product.spu.service.SpuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 3】SPU Service 实现
 * =====================================================================
 *
 * 教学说明：
 * - 当前实现比较基础，重点演示如何按分类查询
 * - 后续会加入更复杂的查询条件（品牌、价格区间、属性筛选等）
 * - 上架/下架操作会影响前端是否可见
 * =====================================================================
 */
@Service
public class SpuServiceImpl extends ServiceImpl<SpuMapper, Spu> implements SpuService {

    @Override
    public List<Spu> listByCategory(Long categoryId) {
        return lambdaQuery()
                .eq(Spu::getCategoryId, categoryId)
                .eq(Spu::getStatus, 1)           // 只查上架的
                .orderByDesc(Spu::getCreateTime)
                .list();
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Spu spu = getById(id);
        if (spu == null) {
            return false;
        }
        spu.setStatus(status);
        return updateById(spu);
    }
}