package com.mall.module.order.task;

import com.mall.config.OrderTimeoutProperties;
import com.mall.module.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单超时自动取消定时任务（Phase 8）
 *
 * 教学要点：
 * - 使用 Spring @Scheduled 实现简单定时扫描
 * - 结合配置类实现可配置的超时策略
 * - 批量处理 + 事务控制
 * - 并发安全设计（与支付、主动取消竞争）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderService orderService;
    private final OrderTimeoutProperties timeoutProperties;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 定时扫描超时订单并自动取消
     */
    @Scheduled(fixedRateString = "${mall.order.timeout-scan-fixed-rate-ms:60000}")
    public void scanTimeoutOrders() {
        LocalDateTime now = LocalDateTime.now();
        int timeoutMinutes = timeoutProperties.getTimeoutMinutes();
        int batchSize = timeoutProperties.getTimeoutBatchSize();

        LocalDateTime timeoutThreshold = now.minusMinutes(timeoutMinutes);

        log.info("========== 订单超时自动取消扫描开始 ==========");
        log.info("当前时间: {}, 超时阈值（{}分钟前）: {}, 批次大小: {}",
                now.format(FORMATTER),
                timeoutMinutes,
                timeoutThreshold.format(FORMATTER),
                batchSize);

        try {
            int cancelledCount = orderService.cancelTimeoutOrders();
            log.info("本次扫描完成，成功取消超时订单数量: {}", cancelledCount);
        } catch (Exception e) {
            log.error("订单超时取消任务执行异常", e);
        }

        log.info("========== 订单超时自动取消扫描结束 ==========");
    }
}
