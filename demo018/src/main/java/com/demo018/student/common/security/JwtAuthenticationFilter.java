package com.demo018.student.common.security;

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
 * 【demo018 核心】JWT 认证过滤器
 * =====================================================================
 *
 * 工作流程：
 * 1. 从请求头 Authorization 中提取 Bearer Token
 * 2. 校验 Token 合法性（签名、过期时间）
 * 3. 如果合法，则将用户信息放入 SecurityContext（Spring Security 上下文）
 * 4. 放行请求，后续 Controller 或方法级 @PreAuthorize 可直接使用认证信息
 *
 * 这是 Spring Security + JWT 无状态认证的标准实现方式。
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

        String token = extractToken(request);

        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            try {
                Long userId = jwtUtil.getUserId(token);
                String username = jwtUtil.getUsername(token);
                Integer role = jwtUtil.getRole(token);

                // 构造 Spring Security 认证对象
                // 这里简单把角色转为 GrantedAuthority（实际项目可扩展）
                String roleName = "ROLE_" + switch (role) {
                    case 1 -> "ADMIN";
                    case 2 -> "TEACHER";
                    default -> "STUDENT";
                };

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority(roleName))
                        );

                // 存入 Security 上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 方便后续业务获取用户ID
                request.setAttribute("currentUserId", userId);
                request.setAttribute("currentUsername", username);

            } catch (Exception e) {
                log.warn("JWT 解析失败: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}