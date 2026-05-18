package com.demo020.student.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * =====================================================================
 * 【demo018 核心】JWT 工具类
 * =====================================================================
 *
 * 功能：
 * - 生成 JWT Token（包含用户ID、用户名、角色等信息）
 * - 校验 Token 是否有效、是否过期
 * - 从 Token 中提取用户信息
 *
 * 教学要点：
 * - JWT 是无状态认证的核心（服务端不保存会话）
 * - Token 通常放在 Authorization: Bearer <token> 头中传递
 * - 签名密钥必须保密（实际项目建议放到配置中心或环境变量）
 * =====================================================================
 */
@Component
public class JwtUtil {

    // 签名密钥（实际项目中请使用更复杂且保密的值）
    @Value("${jwt.secret:demo018-student-secret-key-2026}")
    private String secret;

    // Token 有效期（默认 24 小时）
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    /**
     * 生成 JWT Token
     */
    public String generateToken(Long userId, String username, Integer role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return JWT.create()
                .withIssuer("student-system")
                .withSubject(username)
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(getAlgorithm());
    }

    /**
     * 校验 Token 是否合法
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(getAlgorithm())
                    .withIssuer("student-system")
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserId(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm()).build().verify(token);
        return decodedJWT.getClaim("userId").asLong();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm()).build().verify(token);
        return decodedJWT.getSubject();
    }

    /**
     * 从 Token 中获取角色
     */
    public Integer getRole(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm()).build().verify(token);
        return decodedJWT.getClaim("role").asInt();
    }

    /**
     * 获取 Token 剩余有效期（毫秒）
     */
    public long getRemainingTime(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm()).build().verify(token);
        return decodedJWT.getExpiresAt().getTime() - System.currentTimeMillis();
    }
}