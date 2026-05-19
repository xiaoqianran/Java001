package com.mall.config;

import com.mall.common.log.LoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Web 配置
 *
 * 注册 LoggingFilter（最高优先级），确保所有请求都被完整记录。
 */
@Configuration
public class WebMvcConfig {

    @Bean
    public FilterRegistrationBean<Filter> loggingFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoggingFilter());
        registration.addUrlPatterns("/*");
        registration.setName("loggingFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
