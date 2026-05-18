package com.mall.module.user.controller;

import com.mall.common.result.Result;
import com.mall.module.user.dto.UserRegisterDTO;
import com.mall.module.user.entity.User;
import com.mall.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * Phase 1 Step 2：用户注册接口
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody UserRegisterDTO dto) {
        User registeredUser = userService.register(dto);
        return Result.success("注册成功", registeredUser);
    }
}
