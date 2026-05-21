package com.mall.common.result;

import lombok.Data;

/**
 * 统一响应结果 Result<T>
 *
 * 继承自 demo011 ~ demo020 的成熟设计，所有接口统一返回格式。
 */
@Data
public class Result<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;

    public Result() {}

    public Result(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功 ====================
    public static <T> Result<T> success() {
        return new Result<>(true, 200, "操作成功", null);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(true, 200, message, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, 200, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, 200, message, data);
    }

    // ==================== 失败 ====================
    public static <T> Result<T> error(String message) {
        return new Result<>(false, 500, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(false, code, message, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(false, code, message, data);
    }

    // 链式调用
    public Result<T> code(int code) {
        this.code = code;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }
}
