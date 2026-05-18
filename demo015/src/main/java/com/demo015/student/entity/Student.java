package com.demo015.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * =====================================================================
 * 【demo009】Student 实体 - MyBatis-Plus 版本
 * =====================================================================
 *
 * 相比 demo008，增加了 MyBatis-Plus 注解：
 *
 * @TableName("t_student")
 *   明确指定表名（如果类名和表名不一致时必须加）
 *
 * @TableId(type = IdType.AUTO)
 *   指定主键自增策略
 *
 * 这些注解让 MyBatis-Plus 能更智能地工作。
 * =====================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("t_student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Integer age;
    private Integer gender;
    private String email;
    private String phone;
    private Date createTime;
    private Date updateTime;
}
