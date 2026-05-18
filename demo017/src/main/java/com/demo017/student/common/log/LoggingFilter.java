package com.demo017.student.common.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * =====================================================================
 * 【demo016 完整版】请求 + 响应体日志过滤器（生产推荐写法）
 * =====================================================================
 *
 * 核心能力：
 * 1. 使用 OncePerRequestFilter 保证每个请求只过滤一次
 * 2. 用 ContentCachingRequestWrapper + ContentCachingResponseWrapper 包装请求/响应
 * 3. 可以在 Controller 消费 body 之后，仍然读取到完整的请求体和响应体
 * 4. 自动管理 traceId（放入 MDC）
 * 5. 记录：
 *    - 请求行（方法、URI、Query）
 *    - 请求头（可选）
 *    - 完整 Request Body（JSON / Form）
 *    - 响应状态 + 完整 Response Body
 *    - 耗时
 *
 * 注意事项（教学重点）：
 * - 只有当 Content-Type 是 application/json 或 text/plain 时才打印 body
 * - 为了防止日志爆炸，设置了最大打印长度（MAX_PAYLOAD_LENGTH）
 * - 敏感字段（password、token 等）建议做脱敏（本示例做了简单处理）
 * - 必须在 finally 里调用 responseWrapper.copyBodyToResponse()，否则客户端收不到响应！
 *
 * 这是目前 Spring Boot 项目里最常用、最健壮的「全链路请求响应日志」实现方式。
 * =====================================================================
 */
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 2048; // 最多打印 2KB 的 body，避免日志过大

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 包装请求和响应（关键！）
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        String traceId = TraceIdUtil.generateAndSet();

        try {
            // 记录请求基本信息 + Body
            logRequest(requestWrapper, traceId);

            // 放行请求（Controller、Service 都会在这里执行）
            filterChain.doFilter(requestWrapper, responseWrapper);

            // 记录响应 + Body
            logResponse(responseWrapper, traceId, System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("【请求异常】traceId={} | {} {} | 异常={}",
                    traceId, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
            throw e;
        } finally {
            // 2. 非常重要！把缓存的响应体写回真正的 response，否则前端/客户端收不到数据！
            responseWrapper.copyBodyToResponse();

            // 3. 清理 MDC，防止线程池污染
            TraceIdUtil.clear();
        }
    }

    /**
     * 记录请求信息（含完整 Body）
     */
    private void logRequest(ContentCachingRequestWrapper request, String traceId) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String clientIp = getClientIp(request);

        String body = getContentAsString(request.getContentAsByteArray(), request.getContentType());

        StringBuilder headerInfo = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // 过滤掉敏感头
            if ("authorization".equalsIgnoreCase(headerName) || "cookie".equalsIgnoreCase(headerName)) {
                headerInfo.append(headerName).append("=*** ");
            } else {
                headerInfo.append(headerName).append("=").append(request.getHeader(headerName)).append(" ");
            }
        }

        if (body != null && !body.isBlank()) {
            log.info("【请求开始】traceId={} | {} {} | IP={} | Headers=[{}] | Body={}",
                    traceId, method, uri + (queryString != null ? "?" + queryString : ""),
                    clientIp, headerInfo.toString().trim(), body);
        } else {
            log.info("【请求开始】traceId={} | {} {} | IP={} | Headers=[{}]",
                    traceId, method, uri + (queryString != null ? "?" + queryString : ""),
                    clientIp, headerInfo.toString().trim());
        }
    }

    /**
     * 记录响应信息（含完整 Body）
     */
    private void logResponse(ContentCachingResponseWrapper response, String traceId, long costTime) {
        int status = response.getStatus();
        String uri = response.getHeader("X-Request-URI"); // 备用
        String contentType = response.getContentType();

        String body = getContentAsString(response.getContentAsByteArray(), contentType);

        if (body != null && !body.isBlank()) {
            log.info("【请求完成】traceId={} | 耗时={}ms | status={} | ResponseBody={}",
                    traceId, costTime, status, body);
        } else {
            log.info("【请求完成】traceId={} | 耗时={}ms | status={}", traceId, costTime, status);
        }
    }

    /**
     * byte[] 转字符串（带长度控制 + 敏感信息脱敏）
     */
    private String getContentAsString(byte[] content, String contentType) {
        if (content == null || content.length == 0) {
            return null;
        }

        // 只对 JSON 和文本类型打印 body
        if (contentType == null ||
            (!contentType.contains("application/json") &&
             !contentType.contains("text/plain") &&
             !contentType.contains("application/x-www-form-urlencoded"))) {
            return "[Binary or non-text content, length=" + content.length + "]";
        }

        String body = new String(content, StandardCharsets.UTF_8);

        // 简单敏感字段脱敏（演示用）
        body = body.replaceAll("(\"password\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3");
        body = body.replaceAll("(\"token\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3");

        // 长度截断
        if (body.length() > MAX_PAYLOAD_LENGTH) {
            body = body.substring(0, MAX_PAYLOAD_LENGTH) + "...(truncated)";
        }
        return body;
    }

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