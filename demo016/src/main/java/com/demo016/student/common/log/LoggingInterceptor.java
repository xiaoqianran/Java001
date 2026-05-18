package com.demo016.student.common.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

/**
 * =====================================================================
 * 【demo016 参考】请求响应日志拦截器（Interceptor 版本）
 * =====================================================================
 *
 * 注意：
 * 本类保留作为教学对比。
 * 实际生产中，**推荐使用 LoggingFilter**（OncePerRequestFilter + ContentCachingWrapper），
 * 因为只有 Filter 才能在 body 被 @RequestBody 消费后，仍然读取到完整的请求体和响应体。
 *
 * LoggingFilter 才是本 demo 的主力实现。
 * =====================================================================
 */
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 生成 traceId 并存入 MDC（整条链路可见）
        String traceId = TraceIdUtil.generateAndSet();

        // 2. 记录请求开始时间（用于计算耗时）
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);

        // 3. 打印请求基本信息（带 traceId）
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String clientIp = getClientIp(request);

        StringBuilder params = new StringBuilder();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String[] values = request.getParameterValues(name);
            params.append(name).append("=").append(String.join(",", values)).append(" ");
        }

        log.info("【请求开始】traceId={} | {} {} | IP={} | Query={} | Params=[{}]",
                traceId, method, uri, clientIp,
                queryString != null ? queryString : "",
                params.toString().trim());

        return true; // 放行
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Controller 已执行完毕，可在此记录 ModelAndView 信息（可选）
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

        try {
            String traceId = TraceIdUtil.get();
            long startTime = (Long) request.getAttribute(START_TIME_ATTR);
            long costTime = System.currentTimeMillis() - startTime;

            int status = response.getStatus();
            String uri = request.getRequestURI();

            if (ex != null) {
                log.error("【请求异常】traceId={} | {} | 耗时={}ms | status={} | 异常={}",
                        traceId, uri, costTime, status, ex.getMessage(), ex);
            } else {
                log.info("【请求完成】traceId={} | {} | 耗时={}ms | status={}",
                        traceId, uri, costTime, status);
            }

        } finally {
            // 非常重要！防止线程池复用导致 traceId 串号
            TraceIdUtil.clear();
        }
    }

    /**
     * 获取真实客户端 IP（考虑 Nginx 反向代理等场景）
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}