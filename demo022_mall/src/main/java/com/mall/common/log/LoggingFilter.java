package com.mall.common.log;

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
 * 请求 + 响应体日志过滤器（生产级推荐）
 *
 * 完整记录 Request Body + Response Body + 耗时 + traceId。
 * 这是 demo016 之后最核心的可观测性组件。
 */
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 2048;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        String traceId = TraceIdUtil.generateAndSet();

        try {
            logRequest(requestWrapper, traceId);
            filterChain.doFilter(requestWrapper, responseWrapper);
            logResponse(responseWrapper, traceId, System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("【请求异常】traceId={} | {} {} | 异常={}",
                    traceId, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
            throw e;
        } finally {
            responseWrapper.copyBodyToResponse();
            TraceIdUtil.clear();
        }
    }

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
            if ("authorization".equalsIgnoreCase(headerName) || "cookie".equalsIgnoreCase(headerName)) {
                headerInfo.append(headerName).append("=*** ");
            } else {
                headerInfo.append(headerName).append("=").append(request.getHeader(headerName)).append(" ");
            }
        }

        log.info("【请求】traceId={} | {} {} | ip={} | query={} | headers=[{}] | body={}",
                traceId, method, uri, clientIp, queryString, headerInfo.toString().trim(),
                body.isEmpty() ? "-" : body);
    }

    private void logResponse(ContentCachingResponseWrapper response, String traceId, long costTime) {
        int status = response.getStatus();
        String contentType = response.getContentType();
        String body = getContentAsString(response.getContentAsByteArray(), contentType);

        log.info("【响应】traceId={} | status={} | cost={}ms | body={}",
                traceId, status, costTime, body.isEmpty() ? "-" : body);
    }

    private String getContentAsString(byte[] content, String contentType) {
        if (content == null || content.length == 0) return "";
        if (contentType == null) return "";

        boolean isPrintable = contentType.contains("json") || contentType.contains("text") || contentType.contains("xml");
        if (!isPrintable) return "[binary]";

        String str = new String(content, StandardCharsets.UTF_8);
        if (str.length() > MAX_PAYLOAD_LENGTH) {
            str = str.substring(0, MAX_PAYLOAD_LENGTH) + "...(truncated)";
        }

        // 简单脱敏
        str = str.replaceAll("(\"password\"\\s*:\\s*\")[^\"]+", "$1***")
                 .replaceAll("(\"token\"\\s*:\\s*\")[^\"]+", "$1***");

        return str;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
