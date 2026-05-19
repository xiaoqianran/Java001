package com.mall.module.auth.service.impl;

import com.mall.common.exception.BusinessException;
import com.mall.common.security.JwtUtil;
import com.mall.module.auth.dto.UserLoginDTO;
import com.mall.module.auth.service.AuthService;
import com.mall.module.auth.vo.LoginVO;
import com.mall.module.user.dto.UserRegisterDTO;
import com.mall.module.user.entity.User;
import com.mall.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * =====================================================================
 * 【mall - Phase 1 认证优化】认证服务实现（AuthServiceImpl）
 * =====================================================================
 *
 * 将注册和登录的业务逻辑集中管理，方便未来扩展：
 * - Token 刷新
 * - 登录日志
 * - 多因素认证
 * =====================================================================
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public User register(UserRegisterDTO dto) {
        return userService.register(dto);
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        User user = userService.getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}