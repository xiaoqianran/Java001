package com.mall.module.user.controller;

import com.mall.common.result.Result;
import com.mall.module.user.entity.User;
import com.mall.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * =====================================================================
 * 【demo022_mall - Phase 4 健全性修复】用户管理控制器
 * =====================================================================
 * /api/user/list 仅管理员可访问，且不返回密码。
 *
 * 注意：
 * 用户注册功能已迁移至 /api/auth/register（更符合领域划分）。
 * 此 Controller 目前仅保留用户管理相关接口（未来扩展）。
 * =====================================================================
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 查看所有用户（仅管理员）
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<User>> listAll() {
        List<User> users = userService.list().stream()
                .peek(u -> u.setPassword(null)) // 安全：不返回密码
                .collect(Collectors.toList());
        return Result.success(users);
    }
}
