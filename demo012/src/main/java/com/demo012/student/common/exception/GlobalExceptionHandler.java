package com.demo012.student.common.exception;

import com.demo012.student.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * =====================================================================
 * 【demo011 核心教学】全局异常处理器 GlobalExceptionHandler
 * =====================================================================
 *
 * 这是生产级项目非常重要的组件。
 *
 * 作用：
 *   - 捕获项目中所有未处理的异常
 *   - 统一返回 Result 格式的错误信息
 *   - 避免异常信息直接暴露给调用方（尤其是前端）
 *   - 方便记录日志和排查问题
 *
 * @ControllerAdvice + @ExceptionHandler 是 Spring 处理异常的标准方式。
 *
 * 注意：虽然当前还是控制台程序，但这个类为后续改成 REST API 做了完美准备。
 * =====================================================================
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理所有其他未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}
