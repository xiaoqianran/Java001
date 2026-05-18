package com.demo015.student.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * =====================================================================
 * 【demo013】新增学生请求 DTO + 校验注解
 * =====================================================================
 *
 * 使用 Bean Validation 注解进行参数校验：
 * - @NotBlank：非空且去掉空格后不为空
 * - @Min / @Max：数值范围
 * - @Email：邮箱格式
 * - @Pattern：正则表达式
 *
 * 这些注解会在 Controller 层被 @Valid 触发校验。
 * =====================================================================
 */
@Data
public class StudentCreateDTO {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄最小为1岁")
    @Max(value = 150, message = "年龄最大为150岁")
    private Integer age;

    @NotNull(message = "性别不能为空")
    @Min(value = 0, message = "性别只能是0或1")
    @Max(value = 1, message = "性别只能是0或1")
    private Integer gender;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100)
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
