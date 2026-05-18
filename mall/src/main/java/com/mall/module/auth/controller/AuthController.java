package com.mall.module.auth.controller;

import com.mall.common.exception.BusinessException;
import com.mall.common.result.Result;
import com.mall.common.security.JwtUtil;
import com.mall.common.security.SecurityUtils;
import com.mall.module.auth.dto.UserLoginDTO;
import com.mall.module.auth.vo.LoginVO;
import com.mall.module.user.entity.User;
import com.mall.module.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * =====================================================================
 * 【mall - Phase 1 Step 3 + Step 4 + Step 5】认证控制器
 * =====================================================================
 *
 * 承载了三个步骤的核心对外能力：
 * - Step 3：登录并签发 JWT（/login）
 * - Step 4：演示受保护接口 + 获取当前登录用户（/me）
 * - Step 5：演示方法级权限控制（/admin-only + @PreAuthorize）
 *
 * 教学重点：
 * - 如何使用 @PreAuthorize 进行细粒度权限控制
 * - 开启 @EnableMethodSecurity 后的效果
 * =====================================================================
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     * 成功后返回 JWT Token，客户端后续请求需在 Header 中携带
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        // 1. 根据用户名查询用户（只查状态正常的）
        User user = userService.getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 2. 使用 BCrypt 校验密码（注意：绝不能把明文密码和数据库比对）
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 3. 【核心】生成 JWT Token，把用户关键信息（id、username、role）放进去
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 4. 组装返回结果（不返回密码等敏感信息）
        LoginVO vo = LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();

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
        // 从 SecurityContext 获取用户名（由 Filter 注入）
        String username = SecurityUtils.getCurrentUsername();

        // 从 request attribute 获取更完整的身份信息（Filter 额外放入的）
        Long userId = (Long) request.getAttribute("currentUserId");
        Integer role = (Integer) request.getAttribute("currentUserRole");

        return Result.success("当前用户信息", new MeVO(userId, username, role));
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
