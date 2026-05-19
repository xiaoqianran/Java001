package com.mall.module.refund.controller;

import com.mall.common.exception.BusinessException;
import com.mall.common.result.Result;
import com.mall.common.security.LoginUser;
import com.mall.common.security.SecurityUtils;
import com.mall.module.refund.dto.RefundApplyDTO;
import com.mall.module.refund.service.RefundService;
import com.mall.module.refund.vo.RefundVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 退款申请与审核控制器（Phase 11）
 *
 * 路径设计：
 * - POST /api/refund/apply/{orderId}          买家申请
 * - GET  /api/refund/my                      买家我的申请
 * - GET  /api/refund                         管理员待审列表
 * - POST /api/refund/{id}/approve            管理员通过
 * - POST /api/refund/{id}/reject             管理员拒绝
 */
@RestController
@RequestMapping("/api/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    /**
     * 买家提交退款申请
     */
    @PostMapping("/apply/{orderId}")
    public Result<Long> apply(@PathVariable Long orderId,
                              @Valid @RequestBody RefundApplyDTO dto,
                              HttpServletRequest request) {
        LoginUser buyer = SecurityUtils.getCurrentUser(request);
        Long refundId = refundService.applyRefund(buyer, orderId, dto);
        return Result.success(refundId);
    }

    /**
     * 买家查看自己的退款申请列表
     */
    @GetMapping("/my")
    public Result<List<RefundVO>> myRefunds(HttpServletRequest request) {
        LoginUser buyer = SecurityUtils.getCurrentUser(request);
        List<RefundVO> list = refundService.listMyRefunds(buyer);
        return Result.success(list);
    }

    /**
     * 管理员/卖家查看待审核退款申请列表
     */
    @GetMapping
    public Result<List<RefundVO>> pendingRefunds(HttpServletRequest request) {
        LoginUser admin = SecurityUtils.getCurrentUser(request);
        List<RefundVO> list = refundService.listPendingRefunds(admin);
        return Result.success(list);
    }

    /**
     * 管理员审核通过（触发实际退款）
     */
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id,
                                @RequestParam(required = false) String remark,
                                HttpServletRequest request) {
        LoginUser admin = SecurityUtils.getCurrentUser(request);
        refundService.approveRefund(admin, id, remark);
        return Result.success(null);
    }

    /**
     * 管理员拒绝申请
     */
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id,
                               @RequestBody(required = false) java.util.Map<String, String> body,
                               HttpServletRequest request) {
        LoginUser admin = SecurityUtils.getCurrentUser(request);
        String reason = body != null ? body.get("reason") : null;
        refundService.rejectRefund(admin, id, reason);
        return Result.success(null);
    }

    // 注：reject 使用 Map 接收 body 仅为简单示例，生产可定义专属 RejectDTO
}
