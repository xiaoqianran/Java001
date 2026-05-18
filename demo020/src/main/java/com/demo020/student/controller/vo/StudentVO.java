package com.demo020.student.controller.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Student VO（View Object）
 * 用于返回给前端的数据结构，与 Entity 解耦。
 */
@Data
public class StudentVO {

    private Long id;
    private String studentNo;
    private String name;
    private Integer age;
    private Integer gender;
    private String className;      // 展示班级名称（而非 classId）
    private Integer status;
    private String phone;
    private String email;
    private LocalDateTime createTime;
}