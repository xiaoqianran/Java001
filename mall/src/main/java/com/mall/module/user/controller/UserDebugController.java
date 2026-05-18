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
 * 用户模块调试接口（Phase 1 Step 1 临时验证用）
 *
 * 目的：快速验证 Entity + Mapper + Service + Result + LoggingFilter 全链路是否打通。
 * 后续会删除或替换为正式接口。
 */
@RestController
@RequestMapping("/api/debug/user")
@RequiredArgsConstructor
public class UserDebugController {

    private final UserService userService;

    /**
     * 查看当前数据库中的所有用户（仅开发验证）
     */
    @GetMapping("/list")
    public Result<List<User>> listAll() {
        List<User> users = userService.list();
        return Result.success("用户列表查询成功", users);
    }

    /**
     * 根据用户名查询（演示 getUserByUsername）
     */
    @GetMapping("/by-username")
    public Result<User> getByUsername(String username) {
        User user = userService.getUserByUsername(username);
        return user != null ? Result.success(user) : Result.error(404, "用户不存在或已被禁用");
    }
}
