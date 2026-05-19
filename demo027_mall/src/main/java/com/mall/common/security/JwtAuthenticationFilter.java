package com.mall.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * =====================================================================
 * 【demo021_mall - Phase 1 Step 4】JWT 认证过滤器（核心安全组件）
 * =====================================================================
 *
 * 本步骤核心教学目标：
 * - 把 Step 3 签发的 JWT 真正“用起来”
 * - 实现无状态认证：服务端不保存 Session，每次请求自带身份证明
 * - 将 JWT 中的用户信息转换为 Spring Security 的 Authentication 对象
 *
 * 与 Step 3 的区别：
 * - Step 3 只负责“生成 Token”（登录成功后返回）
 * - Step 4 负责“验证 + 解析 + 注入上下文”（每次请求都要做）
 *
 * 关键设计点：
 * 1. 继承 OncePerRequestFilter：保证每个请求只过滤一次
 * 2. 在 UsernamePasswordAuthenticationFilter 之前执行（见 SecurityConfig）
 * 3. 解析成功后把 Authentication 放入 SecurityContextHolder
 * 4. 同时把 userId/username/role 放到 request attribute，方便业务层快速获取
 *
 * 教学重点：
 * - 为什么要把角色加上 "ROLE_" 前缀？
 * - SecurityContextHolder 是如何在线程间传递认证信息的？
 * - 为什么即使 Token 无效也不抛异常，而是静默放行？
 *
 * 后续演进方向（demo022+）：
 * - 可以扩展为支持多种认证方式（JWT + OAuth2）
 * - 可以把用户信息封装成自定义 LoginUser 对象（更面向领域）
 * =====================================================================
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ====================== 核心逻辑开始 ======================
        // 1. 从请求头提取 Token（格式：Bearer xxxxx）
        String token = extractToken(request);

        // 2. Token 存在且合法时才进行认证
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            try {
                // 3. 从 Token 中解析出关键身份信息
                Long userId = jwtUtil.getUserId(token);
                String username = jwtUtil.getUsername(token);
                Integer role = jwtUtil.getRole(token);

                // 4. 将角色转换为 Spring Security 能识别的 GrantedAuthority
                //    注意：Spring Security 要求权限名称必须以 "ROLE_" 开头
                String roleName = switch (role) {
                    case 1 -> "ROLE_ADMIN";
                    case 2 -> "ROLE_SELLER";
                    default -> "ROLE_BUYER";
                };

                // 5. 构造 Authentication 对象（principal 用 username，credentials 为 null）
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority(roleName))
                        );

                // 6. 【最关键的一行 - Step 5 增强】把认证信息放入 SecurityContext
                //    这里我们把 Authentication 的 principal 从简单的 username 升级为 LoginUser 对象
                //    这样 Service 层调用 SecurityUtils.getCurrentUser() 时能拿到更完整的信息
                LoginUser loginUser = LoginUser.builder()
                        .userId(userId)
                        .username(username)
                        .role(role)
                        .build();

                UsernamePasswordAuthenticationToken enhancedAuth =
                        new UsernamePasswordAuthenticationToken(
                                loginUser,                    // 【关键改动】principal 改成 LoginUser
                                null,
                                authentication.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(enhancedAuth);

                // 7. 额外放到 request attribute，方便 Controller 快速取用
                request.setAttribute("currentUserId", userId);
                request.setAttribute("currentUsername", username);
                request.setAttribute("currentUserRole", role);

            } catch (Exception e) {
                // Token 解析失败（过期、篡改等）时不抛异常，保持匿名状态
                log.warn("JWT 解析失败: {}", e.getMessage());
            }
        }
        // ====================== 核心逻辑结束 ======================

        // 放行请求，无论是否认证成功
        filterChain.doFilter(request, response);
    }

    /**
     * 从 Authorization 头中提取 Bearer Token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
