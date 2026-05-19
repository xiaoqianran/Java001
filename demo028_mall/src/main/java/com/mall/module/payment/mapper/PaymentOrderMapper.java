package com.mall.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.module.payment.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
}
