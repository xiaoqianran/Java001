package com.mall.module.user.controller;

import com.mall.common.result.Result;
import com.mall.module.user.entity.User;
import com.mall.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * =====================================================================
 * 【demo021_mall - Phase 1 后续优化】用户管理控制器（已迁移注册功能）
 * =====================================================================
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
     * 临时调试接口：查看所有用户（仅开发环境使用）
     */
    @GetMapping("/list")
    public Result<List<User>> listAll() {
        return Result.success(userService.list());
    }
}
