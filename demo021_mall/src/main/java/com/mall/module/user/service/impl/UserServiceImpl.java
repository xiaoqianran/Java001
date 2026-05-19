package com.mall.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.exception.BusinessException;
import com.mall.module.user.dto.UserRegisterDTO;
import com.mall.module.user.entity.User;
import com.mall.module.user.mapper.UserMapper;
import com.mall.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现
 *
 * 继承 ServiceImpl 获得 MyBatis-Plus 提供的强大 CRUD 封装（demo010 的成熟做法）。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getStatus, 1)
                .eq(User::getDeleted, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(UserRegisterDTO dto) {
        // 1. 检查用户名是否已存在
        if (lambdaQuery().eq(User::getUsername, dto.getUsername()).exists()) {
            throw new BusinessException(400, "用户名已被占用");
        }

        // 2. 检查手机号是否已存在（如果提供了手机号）
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            if (lambdaQuery().eq(User::getPhone, dto.getPhone()).exists()) {
                throw new BusinessException(400, "该手机号已被注册");
            }
        }

        // 3. 构建用户实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // BCrypt 加密
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(3);      // 默认普通买家
        user.setStatus(1);    // 正常状态

        // 4. 保存
        boolean success = this.save(user);
        if (!success) {
            throw new BusinessException("注册失败，请稍后重试");
        }

        // 5. 返回时清空密码（安全）
        user.setPassword(null);
        return user;
    }
}
