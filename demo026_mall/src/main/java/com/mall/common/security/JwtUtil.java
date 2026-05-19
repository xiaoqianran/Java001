package com.mall.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * =====================================================================
 * 【demo021_mall - Phase 1 Step 3】JWT 工具类（Token 签发与解析）
 * =====================================================================
 *
 * 本步骤核心变化：
 * - 封装 JWT 的生成、验证、解析逻辑
 * - 使用 Auth0 的 java-jwt 库（与 demo018/020 保持一致）
 *
 * 教学重点：
 * - JWT 的三部分结构（Header + Payload + Signature）
 * - Claim（声明）的作用：把 userId、role 等信息安全地放在客户端
 * - 为什么签名密钥（secret）必须保密？
 * - issuer（颁发者）的意义
 *
 * 注意事项：
 * - 当前 secret 写在配置文件，生产环境应使用环境变量或配置中心
 * - expiration 建议根据业务调整（商城通常 2~24 小时）
 * =====================================================================
 */
@Component
public class JwtUtil {

    // 从 application.yml 读取，可热更新（简化版）
    @Value("${jwt.secret:mall-secret-key-change-in-production-2026}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    // 使用 HMAC256 对称加密算法
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    /**
     * 生成 JWT Token（登录成功后调用）
     */
    public String generateToken(Long userId, String username, Integer role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return JWT.create()
                .withIssuer("mall-system")           // 颁发者
                .withSubject(username)               // 主题（常用作用户名）
                .withClaim("userId", userId)         // 自定义声明：用户ID
                .withClaim("username", username)     // 自定义声明：用户名
                .withClaim("role", role)             // 自定义声明：角色
                .withIssuedAt(now)                   // 签发时间
                .withExpiresAt(expireDate)           // 过期时间
                .sign(getAlgorithm());               // 签名
    }

    /**
     * 校验 Token 是否合法（签名正确 + 未过期）
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(getAlgorithm())
                    .withIssuer("mall-system")
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            // 包括签名错误、过期、issuer 不匹配等情况
            return false;
        }
    }

    // ==================== 从 Token 中提取信息 ====================

    public Long getUserId(String token) {
        DecodedJWT decoded = JWT.require(getAlgorithm()).build().verify(token);
        return decoded.getClaim("userId").asLong();
    }

    public String getUsername(String token) {
        DecodedJWT decoded = JWT.require(getAlgorithm()).build().verify(token);
        return decoded.getSubject();
    }

    public Integer getRole(String token) {
        DecodedJWT decoded = JWT.require(getAlgorithm()).build().verify(token);
        return decoded.getClaim("role").asInt();
    }
}
