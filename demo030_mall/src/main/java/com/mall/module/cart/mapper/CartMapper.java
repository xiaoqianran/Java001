package com.mall.module.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.module.cart.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车 Mapper
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}