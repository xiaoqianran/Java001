package com.mall.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * User Mapper
 *
 * 继承 BaseMapper 获得最基础的 CRUD 能力（Phase 1 Step 1 的核心收获）。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
