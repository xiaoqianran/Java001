package com.mall.common.log;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * TraceId 工具类（MDC 上下文）
 *
 * 所有日志自动带上 traceId，实现全链路可追踪。
 * 继承自 demo016 的成熟实现。
 */
public class TraceIdUtil {

    public static final String TRACE_ID_KEY = "traceId";

    public static String generateAndSet() {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        MDC.put(TRACE_ID_KEY, traceId);
        return traceId;
    }

    public static void set(String traceId) {
        if (traceId != null && !traceId.isBlank()) {
            MDC.put(TRACE_ID_KEY, traceId);
        }
    }

    public static String get() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static void clear() {
        MDC.remove(TRACE_ID_KEY);
        MDC.clear();
    }
}
