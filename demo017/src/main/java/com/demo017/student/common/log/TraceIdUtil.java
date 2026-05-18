package com.demo017.student.common.log;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * =====================================================================
 * 【demo016 核心】TraceId 工具类 - MDC 上下文管理
 * =====================================================================
 *
 * 作用：
 *   - 统一生成和传递 traceId（请求链路唯一标识）
 *   - 所有日志自动带上 traceId，方便在分布式/高并发场景下串联一次请求的所有日志
 *
 * 使用方式：
 *   - 在拦截器 preHandle 中调用 generateAndSet()
 *   - 在业务代码中可直接 MDC.get(TRACE_ID_KEY) 获取当前 traceId
 *   - 请求结束后必须调用 clear() 防止线程池复用导致污染
 *
 * 生产建议：
 *   - 真实项目可用雪花算法或 ULID 代替 UUID
 *   - 可同时放入 userId、ip、请求来源等更多维度信息
 * =====================================================================
 */
public class TraceIdUtil {

    public static final String TRACE_ID_KEY = "traceId";

    /**
     * 生成 traceId 并放入 MDC（推荐格式：前8位短 UUID）
     */
    public static String generateAndSet() {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        MDC.put(TRACE_ID_KEY, traceId);
        return traceId;
    }

    /**
     * 手动设置 traceId（用于测试或特殊场景）
     */
    public static void set(String traceId) {
        if (traceId != null && !traceId.isBlank()) {
            MDC.put(TRACE_ID_KEY, traceId);
        }
    }

    /**
     * 获取当前 traceId
     */
    public static String get() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * 清除 MDC（必须在请求结束时调用，避免线程复用污染）
     */
    public static void clear() {
        MDC.remove(TRACE_ID_KEY);
        MDC.clear();
    }
}