package com.mall.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体（对应 sys_user 表）
 *
 * Phase 1 核心领域对象。
 * 角色设计：1=ADMIN（平台管理员）, 2=SELLER（商家）, 3=BUYER（普通买家）
 */
@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    /** 1=管理员, 2=商家, 3=普通买家 */
    private Integer role;

    /** 0=禁用, 1=正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
