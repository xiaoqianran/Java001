package com.mall.module.auth.service;

import com.mall.module.auth.dto.UserLoginDTO;
import com.mall.module.auth.vo.LoginVO;
import com.mall.module.user.dto.UserRegisterDTO;
import com.mall.module.user.entity.User;

/**
 * =====================================================================
 * 【demo021_mall - Phase 1 认证优化】认证服务接口（AuthService）
 * =====================================================================
 *
 * 本次重构核心变化：
 * - 将认证相关业务逻辑从 Controller 剥离到 Service 层
 * - 注册功能从 /api/user/register 迁移至 /api/auth/register
 *
 * 教学重点：
 * - 领域驱动：认证（Auth）是一个独立的关注点
 * - 单一职责：Controller 只负责参数校验和返回，业务逻辑下沉
 * =====================================================================
 */
public interface AuthService {

    /**
     * 用户注册
     */
    User register(UserRegisterDTO dto);

    /**
     * 用户登录，返回 JWT Token
     */
    LoginVO login(UserLoginDTO dto);
}