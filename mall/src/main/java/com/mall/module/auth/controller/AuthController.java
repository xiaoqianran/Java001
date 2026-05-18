package com.mall.module.auth.controller;

import com.mall.common.result.Result;
import com.mall.common.security.SecurityUtils;
import com.mall.module.auth.dto.UserLoginDTO;
import com.mall.module.user.dto.UserRegisterDTO;
import com.mall.module.auth.service.AuthService;
import com.mall.module.auth.vo.LoginVO;
import com.mall.module.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * =====================================================================
 * 【mall - Phase 1 认证重构】认证控制器（优化版）
 * =====================================================================
 *
 * 本次重构核心变化（对应 A 选项）：
 * - 注册接口迁移：从 `/api/user/register` → `/api/auth/register`
 * - 引入 AuthService，解耦业务逻辑
 * - `/me` 接口返回完整的 `LoginUser` 对象
 *
 * 领域划分更清晰：Auth（认证）作为一个独立模块。
 * =====================================================================
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册（已从 /api/user/register 迁移至此处）
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody UserRegisterDTO dto) {
        User user = authService.register(dto);
        return Result.success("注册成功", user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        LoginVO vo = authService.login(dto);
        return Result.success("登录成功", vo);
    }

    /**
     * 获取当前登录用户信息（Step 4 核心演示接口）
     *
     * 调用方式：
     *   GET /api/auth/me
     *   Header: Authorization: Bearer <token>
     *
     * 这个接口证明了 JwtAuthenticationFilter 已经成功把用户信息注入到了上下文中。
     */
    @GetMapping("/me")
    public Result<Object> me(HttpServletRequest request) {
        // 【Phase 1 Step 5 优化】使用增强后的 SecurityUtils 获取完整 LoginUser 信息
        var loginUser = SecurityUtils.getCurrentUser(request);

        if (loginUser == null) {
            return Result.error(401, "未登录或 Token 无效");
        }

        return Result.success("当前用户信息", loginUser);
    }

    // 使用 Java 14+ record 快速定义返回 VO（教学中可以演示现代 Java 写法）
    record MeVO(Long userId, String username, Integer role) {}

    /**
     * 【Step 5 新增演示】只有管理员才能访问的接口
     *
     * 使用 @PreAuthorize 进行方法级权限控制
     * 这是电商系统中后台管理功能最常用的权限方式
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")   // 只有角色为 ADMIN 的用户才能调用
    public Result<String> adminOnly() {
        return Result.success("恭喜，你是管理员，可以访问此接口");
    }
}
