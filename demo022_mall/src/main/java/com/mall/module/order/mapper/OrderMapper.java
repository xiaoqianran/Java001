package com.mall.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.module.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper（Phase 4 - demo022_mall）
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}