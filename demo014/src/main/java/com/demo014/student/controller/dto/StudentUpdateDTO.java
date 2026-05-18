package com.demo014.student.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 更新学生请求 DTO
 */
@Data
public class StudentUpdateDTO {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50)
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(1) @Max(150)
    private Integer age;

    @NotNull(message = "性别不能为空")
    @Min(0) @Max(1)
    private Integer gender;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
