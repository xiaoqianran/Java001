package com.mall.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * =====================================================================
 * 【demo022_mall - Phase 4 健全性修复】安全工具类
 * =====================================================================
 * 已适配 JwtAuthenticationFilter 将 LoginUser 直接放入 principal 的情况。
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

        // 优先：JwtAuthenticationFilter 已将完整 LoginUser 放入 principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }

        // 兜底：从 request attribute 构造（Controller 层可用）
        // 注意：纯 Service 层调用时拿不到 request，这里返回基本信息
        return LoginUser.builder()
                .username(authentication.getName())
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

        // 优先返回 Filter 中放入的完整 LoginUser
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }

        // 兜底使用 request attribute
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

        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return ((LoginUser) principal).getUsername();
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
