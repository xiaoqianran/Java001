package com.mall.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.module.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项 Mapper（Phase 4 - demo022_mall）
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}