package com.mall.config;

import com.mall.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * =====================================================================
 * 【demo021_mall - Phase 1 Step 4】Spring Security 配置（安全总控）
 * =====================================================================
 *
 * 本步骤核心变化：
 * - 不再使用默认的表单登录，而是完全基于 JWT
 * - 把 JwtAuthenticationFilter 插入到过滤器链的正确位置
 * - 精确控制哪些接口公开，哪些需要认证
 *
 * 教学重点：
 * - 为什么要把 JWT Filter 放在 UsernamePasswordAuthenticationFilter 之前？
 * - SessionCreationPolicy.STATELESS 的含义和必要性
 * - requestMatchers 的匹配顺序和最佳实践
 *
 * 与传统 Session 认证的本质区别：
 * - Session 方式：服务端保存登录状态，客户端只存 sessionId
 * - JWT 方式：服务端无状态，客户端每次携带 Token，服务端每次验证
 * =====================================================================
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // 【Step 5 新增】开启方法级安全注解，支持 @PreAuthorize、@Secured 等
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 关闭 CSRF（JWT 项目通常不需要）
            .csrf(csrf -> csrf.disable())

            // 2. 【关键配置】完全无状态，不创建也不使用 HttpSession
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. 配置 URL 访问权限
            .authorizeHttpRequests(auth -> auth
                // 公开接口：登录、注册、健康检查（注意：注册已迁移至 /api/auth/register）
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/actuator/**",
                    "/error",
                    "/api/payment/mock-callback"
                ).permitAll()

                // 其余所有接口必须经过认证（即必须带合法 Token）
                .anyRequest().authenticated()
            )

            // 4. 【最重要的一行】把我们自定义的 JWT 过滤器放在标准认证过滤器之前
            //    这样当请求进来时，先由 JwtAuthenticationFilter 尝试解析 Token 并设置认证信息
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}