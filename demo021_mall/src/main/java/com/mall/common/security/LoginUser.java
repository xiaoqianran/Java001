package com.mall.common.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =====================================================================
 * 【mall - Phase 1 Step 5】当前登录用户领域对象（LoginUser）
 * =====================================================================
 *
 * 本步骤核心变化：
 * - 不再让 Controller 直接操作 userId、username、role 这些散落信息
 * - 引入统一的 LoginUser 对象，代表“当前已登录的用户”
 *
 * 教学重点：
 * - 为什么要把当前用户封装成对象？（面向领域、类型安全、易扩展）
 * - 后续可以让 LoginUser 实现 UserDetails 接口，从而与 Spring Security 深度整合
 * - 在 Service 层直接注入或通过工具类获取 LoginUser，是生产项目的常见做法
 *
 * 演进路径（参考 demo020 重构思路）：
 *   Step 4：通过 request attribute + SecurityContext 拿到零散信息
 *   Step 5：封装成 LoginUser 对象（当前步骤）
 *   未来：实现 UserDetails + 自定义 Authentication，实现更标准的 Spring Security 用法
 * =====================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    /** 用户ID */
    private Long userId;

    /** 登录用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 角色（1=ADMIN, 2=SELLER, 3=BUYER） */
    private Integer role;

    /**
     * 判断当前用户是否为管理员
     */
    public boolean isAdmin() {
        return role != null && role == 1;
    }

    /**
     * 判断当前用户是否为商家
     */
    public boolean isSeller() {
        return role != null && role == 2;
    }

    /**
     * 判断当前用户是否为普通买家
     */
    public boolean isBuyer() {
        return role != null && role == 3;
    }
}
