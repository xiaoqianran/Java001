package com.demo008.student.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * =====================================================================
 * 【demo003 核心改动点】Student 实体 —— Lombok 版本
 * =====================================================================
 *
 * 这是整个 demo003 **唯一** 和 demo002 不同的文件！
 *
 * 教学目的非常明确：
 *   - 让你看到「只改一个文件」就能省掉 60 多行代码
 *   - 其他所有层（Controller、Service、Mapper）代码完全不需要动
 *   - 这就是 Lombok 的「低侵入」优点
 *
 * @Data + @NoArgsConstructor + @AllArgsConstructor + @Builder
 * 组合是目前 Java 项目里最常见的 Entity 写法。
 * =====================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    private Long id;
    private String name;
    private Integer age;
    private Integer gender;
    private String email;
    private String phone;
    private Date createTime;
    private Date updateTime;
}
