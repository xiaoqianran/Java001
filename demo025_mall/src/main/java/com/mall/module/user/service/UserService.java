package com.mall.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.module.user.dto.UserRegisterDTO;
import com.mall.module.user.entity.User;

/**
 * 用户服务接口
 *
 * Phase 1 基础服务能力。
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询有效用户（登录时使用）
     */
    User getUserByUsername(String username);

    /**
     * 用户注册
     * @return 注册成功的用户（不含密码）
     */
    User register(UserRegisterDTO dto);
}
