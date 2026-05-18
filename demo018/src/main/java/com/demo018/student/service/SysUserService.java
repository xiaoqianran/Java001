package com.demo018.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo018.student.entity.SysUser;

/**
 * SysUser Service
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户（用于登录）
     */
    SysUser getUserByUsername(String username);
}