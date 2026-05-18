package com.demo020.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体（对应 sys_user 表）
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    private Integer role;      // 1=管理员, 2=教师, 3=学生

    private Integer status;

    private Long studentId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}