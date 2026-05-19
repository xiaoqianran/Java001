package com.mall.common.exception;

/**
 * 业务异常
 *
 * 专门用于表示业务逻辑错误（区别于系统异常）。
 * 配合 GlobalExceptionHandler 使用。
 */
public class BusinessException extends RuntimeException {

    private int code = 500;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
