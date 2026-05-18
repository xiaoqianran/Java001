package com.demo020.student.config;

import com.demo020.student.common.log.LoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * =====================================================================
 * 【demo016 完整版】Web 配置 - 注册 LoggingFilter（支持完整请求/响应体）
 * =====================================================================
 *
 * 使用 FilterRegistrationBean 注册 LoggingFilter：
 * - order 设置为最高优先级（最小值），确保最先执行
 * - 可以精确控制哪些路径需要记录日志
 *
 * 为什么用 Filter 而不是只用 Interceptor？
 * - 只有 Filter + ContentCachingWrapper 才能在 body 被消费后仍然读取到完整内容
 * - 这是目前业界最标准的「全量请求响应日志」做法
 * =====================================================================
 */
@Configuration
public class WebMvcConfig {

    @Bean
    public FilterRegistrationBean<Filter> loggingFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoggingFilter());
        registration.addUrlPatterns("/*");           // 拦截所有请求
        registration.setName("loggingFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 最高优先级
        return registration;
    }
}