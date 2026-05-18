package com.demo020.student.common.exception;

/**
 * =====================================================================
 * 【demo011】业务异常 BusinessException
 * =====================================================================
 *
 * 作用：
 *   - 专门用于表示业务逻辑错误（比如“学生姓名不能为空”、“该学生已存在”等）
 *   - 与系统异常（NullPointer、数据库连接失败等）区分开来
 *
 * 配合 GlobalExceptionHandler 使用后，可以统一返回友好的错误信息。
 *
 * 示例：
 *   throw new BusinessException("学生姓名不能为空");
 *   throw new BusinessException(400, "参数错误");
 * =====================================================================
 */
public class BusinessException extends RuntimeException {

    private int code = 500;   // 默认错误码

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
