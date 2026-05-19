package com.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 订单超时配置（Phase 8）
 */
@Data
@Component
@ConfigurationProperties(prefix = "mall.order")
public class OrderTimeoutProperties {

    /** 超时时间（分钟） */
    private Integer timeoutMinutes = 30;

    /** 扫描频率（毫秒） */
    private Long timeoutScanFixedRateMs = 60000L;

    /** 每次扫描处理的最大数量 */
    private Integer timeoutBatchSize = 100;
}
