package com.mall.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * =====================================================================
 * 【mall - Phase 1 Step 5】安全工具类（当前用户获取增强版）
 * =====================================================================
 *
 * 本步骤核心变化：
 * - 新增 `getCurrentUser()` 方法，返回 `LoginUser` 对象
 * - Service 层现在可以直接调用 `SecurityUtils.getCurrentUser()` 获取当前登录用户
 * - 同时保留对 HttpServletRequest 的支持（向后兼容）
 *
 * 教学重点：
 * - 为什么要把当前用户封装成 LoginUser？
 * - 如何在 Service 层无感知地获取当前登录用户？
 * - 匿名用户（未登录）返回 null 的处理策略
 *
 * 使用示例（在任意 Service 中）：
 *   LoginUser currentUser = SecurityUtils.getCurrentUser();
 *   if (currentUser != null && currentUser.isAdmin()) { ... }
 * =====================================================================
 */
public class SecurityUtils {

    private SecurityUtils() {}

    /**
     * 【Step 5 新增核心方法】
     * 获取当前登录用户（推荐在 Service 层使用）
     *
     * 注意：当前实现依赖 JwtAuthenticationFilter 向 SecurityContext 存入的 principal（username）。
     *      更完整的做法是后续让 JwtAuthenticationFilter 存入 LoginUser 对象本身。
     */
    public static LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 当前阶段 principal 是 username（字符串）
        // 更理想的做法是让 Filter 直接把 LoginUser 作为 principal 存入
        String username = authentication.getName();

        // 尝试从 request attribute 补充信息（Controller 层可用）
        // 注意：Service 层调用时通常拿不到 request，这里先返回基本信息
        return LoginUser.builder()
                .username(username)
                .build();
    }

    /**
     * 增强版：结合 request attribute，尽量构造完整的 LoginUser
     * 主要供 Controller 层调用
     */
    public static LoginUser getCurrentUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object userIdAttr = request.getAttribute("currentUserId");
        Object roleAttr = request.getAttribute("currentUserRole");

        return LoginUser.builder()
                .userId(userIdAttr != null ? (Long) userIdAttr : null)
                .username(authentication.getName())
                .role(roleAttr != null ? (Integer) roleAttr : null)
                .build();
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * 判断当前请求是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
