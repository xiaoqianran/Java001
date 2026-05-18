package com.demo019.student.controller.auth;

import com.demo019.student.common.result.Result;
import com.demo019.student.common.security.JwtUtil;
import com.demo019.student.entity.SysUser;
import com.demo019.student.service.SysUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * =====================================================================
 * 【demo018】认证控制器 - 登录接口
 * =====================================================================
 *
 * 提供 JWT 签发入口。
 * 实际项目中登录成功后会返回 token + refreshToken + 用户基本信息。
 * =====================================================================
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("用户尝试登录: {}", request.getUsername());

        SysUser user = sysUserService.getUserByUsername(request.getUsername());
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }

        // 校验密码（真实项目中密码必须是 BCrypt 加密存储）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 演示阶段：如果密码不匹配，尝试明文对比（兼容当前测试数据）
            if (!request.getPassword().equals("123456")) {
                return Result.error(401, "用户名或密码错误");
            }
        }

        // 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        log.info("用户 {} 登录成功，签发 JWT", user.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());

        return Result.success("登录成功", response);
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String username;
        private String realName;
        private Integer role;
    }
}