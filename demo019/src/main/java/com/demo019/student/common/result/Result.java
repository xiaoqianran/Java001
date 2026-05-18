package com.demo019.student.common.result;

import lombok.Data;

/**
 * =====================================================================
 * 【demo011 核心】统一响应结果 Result<T>
 * =====================================================================
 *
 * 这是生产级项目必备的工具类。
 *
 * 作用：
 *   - 让所有接口返回格式统一
 *   - 包含：成功/失败状态、状态码、提示信息、数据
 *
 * 常见使用方式：
 *   Result.success(data)          // 成功并携带数据
 *   Result.success()              // 成功无数据
 *   Result.error("错误信息")      // 失败
 *   Result.error(500, "服务器错误")
 *
 * 配合全局异常处理器后，前端/控制台可以很方便地判断请求是否成功。
 * =====================================================================
 */
@Data
public class Result<T> {

    /** 是否成功 */
    private boolean success;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 返回数据 */
    private T data;

    // ==================== 构造方法 ====================

    public Result() {
    }

    public Result(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功静态方法 ====================

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

    // ==================== 失败静态方法 ====================

    public static <T> Result<T> error(String message) {
        return new Result<>(false, 500, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(false, code, message, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(false, code, message, data);
    }

    // ==================== 自定义构建（链式调用） ====================

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
