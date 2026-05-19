package com.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * 提供一个空的 UserDetailsService，避免 Spring Security 启动时生成默认密码警告。
 * 实际认证全部走 JWT，不依赖此 Service。
 */
@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        // 返回一个空的 InMemory 实现，防止默认密码警告
        return new InMemoryUserDetailsManager();
    }
}
