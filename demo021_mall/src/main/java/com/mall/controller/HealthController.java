package com.mall.controller;

import com.mall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 *
 * 用于验证骨架是否正常工作。
 * 后续会被更专业的 Actuator 健康检查 + 业务健康检查替代。
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("mall service is healthy", "ok");
    }
}
