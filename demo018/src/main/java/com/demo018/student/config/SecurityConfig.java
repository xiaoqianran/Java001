package com.demo018.student.config;

import com.demo018.student.common.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * =====================================================================
 * 【demo018 核心】Spring Security 配置
 * =====================================================================
 *
 * 教学要点：
 * - 无状态（STATELESS）会话策略（JWT 核心理念）
 * - 放行登录接口 /auth/login 和公开接口
 * - 其他所有接口都需要 JWT 认证
 * - 注入 JwtAuthenticationFilter 在 UsernamePasswordAuthenticationFilter 之前执行
 * - 提供 PasswordEncoder（BCrypt）供登录验证使用
 * =====================================================================
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 支持 @PreAuthorize 注解
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（JWT 无状态，不需要）
            .csrf(AbstractHttpConfigurer::disable)

            // 禁用 Session（无状态认证）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 配置请求授权规则
            .authorizeHttpRequests(auth -> auth
                // 登录接口允许匿名访问
                .requestMatchers("/auth/login").permitAll()
                // 健康检查、错误页放行
                .requestMatchers("/error", "/actuator/**").permitAll()
                // 其他所有接口都需要认证
                .anyRequest().authenticated()
            )

            // 把 JWT 过滤器放在 UsernamePasswordAuthenticationFilter 之前
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码编码器（BCrypt）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}