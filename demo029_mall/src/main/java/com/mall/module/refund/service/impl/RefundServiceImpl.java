package com.mall.module.refund.service.impl;

import com.mall.common.exception.BusinessException;
import com.mall.common.security.LoginUser;
import com.mall.module.order.entity.Order;
import com.mall.module.order.enums.OrderStatus;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.payment.service.PaymentService;
import com.mall.module.refund.dto.RefundApplyDTO;
import com.mall.module.refund.entity.RefundOrder;
import com.mall.module.refund.mapper.RefundOrderMapper;
import com.mall.module.refund.service.RefundService;
import com.mall.module.refund.vo.RefundVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 退款申请与审核服务实现（Phase 11）
 *
 * 核心原则：
 * - 申请仅创建记录，不触碰订单/支付/库存
 * - 审核通过后委托 PaymentService 执行退款（复用 Phase 10 成熟逻辑 + 条件更新 + 事务）
 * - 所有角色校验与业务校验下沉到 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefundServiceImpl implements RefundService {

    private final RefundOrderMapper refundOrderMapper;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;

    // ==================== 买家端：申请 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyRefund(LoginUser buyer, Long orderId, RefundApplyDTO dto) {
        if (buyer == null || buyer.getUserId() == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (orderId == null || dto == null || !StringUtils.hasText(dto.getReason())) {
            throw new BusinessException("参数不完整");
        }

        // 1. 校验订单存在 + 归属 + 状态
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // BUYER 必须是自己的订单（ADMIN 申请入口暂不开放，走直接退款）
        if (!order.getUserId().equals(buyer.getUserId())) {
            throw new BusinessException(403, "只能为自己的订单申请退款");
        }
        if (!OrderStatus.fromCode(order.getStatus()).canRefund()) {
            throw new BusinessException("只有已支付(20)且未发货的订单可以申请退款，当前状态：" + order.getStatus());
        }

        // 2. 校验是否已有任何退款申请（本阶段一订单只允许一条记录，拒绝后不重复申请；用唯一约束防并发）
        long existsCount = refundOrderMapper.selectCount(
                new LambdaQueryWrapper<RefundOrder>()
                        .eq(RefundOrder::getOrderId, orderId)
        );
        if (existsCount > 0) {
            throw new BusinessException("该订单已存在退款申请，请勿重复提交");
        }

        // 3. 创建申请记录（status=10 待审核）
        RefundOrder apply = new RefundOrder();
        apply.setOrderId(orderId);
        apply.setUserId(buyer.getUserId());
        apply.setReason(dto.getReason().trim());
        apply.setStatus(10); // 待审核
        apply.setApplyTime(LocalDateTime.now());

        try {
            refundOrderMapper.insert(apply);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new BusinessException("该订单已存在退款申请，请勿重复提交");
        }

        log.info("退款申请创建成功，refundId={}, orderId={}, userId={}", apply.getId(), orderId, buyer.getUserId());
        return apply.getId();
    }

    // ==================== 买家端：我的申请列表 ====================

    @Override
    public List<RefundVO> listMyRefunds(LoginUser buyer) {
        if (buyer == null || buyer.getUserId() == null) {
            throw new BusinessException(401, "请先登录");
        }

        List<RefundOrder> list = refundOrderMapper.selectList(
                new LambdaQueryWrapper<RefundOrder>()
                        .eq(RefundOrder::getUserId, buyer.getUserId())
                        .orderByDesc(RefundOrder::getApplyTime)
        );

        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // ==================== 管理员端：待审列表 ====================

    @Override
    public List<RefundVO> listPendingRefunds(LoginUser admin) {
        // 简单角色校验（1=ADMIN, 2=SELLER）
        if (admin == null || admin.getRole() == null || (admin.getRole() != 1 && admin.getRole() != 2)) {
            throw new BusinessException(403, "仅管理员/卖家可查看待审核退款申请");
        }

        List<RefundOrder> list = refundOrderMapper.selectList(
                new LambdaQueryWrapper<RefundOrder>()
                        .eq(RefundOrder::getStatus, 10)
                        .orderByAsc(RefundOrder::getApplyTime)
        );

        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // ==================== 管理员：审核通过 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveRefund(LoginUser admin, Long refundId, String remark) {
        if (admin == null || admin.getRole() == null || (admin.getRole() != 1 && admin.getRole() != 2)) {
            throw new BusinessException(403, "仅管理员/卖家可审核退款申请");
        }

        RefundOrder refund = refundOrderMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException("退款申请不存在");
        }
        if (refund.getStatus() != 10) {
            throw new BusinessException("只能审核状态为【待审核】的申请，当前状态：" + refund.getStatus());
        }

        // 先条件更新 refund_order 状态为已通过（抢占审核权），防止并发审核人同时通过
        // 影响行数 !=1 说明状态已变（被他人抢占或异常），直接失败，不执行退款
        int affected = refundOrderMapper.update(null,
                new LambdaUpdateWrapper<RefundOrder>()
                        .eq(RefundOrder::getId, refundId)
                        .eq(RefundOrder::getStatus, 10)
                        .set(RefundOrder::getStatus, 20)
                        .set(RefundOrder::getReviewerId, admin.getUserId())
                        .set(RefundOrder::getReviewTime, LocalDateTime.now())
                        .set(RefundOrder::getReviewRemark, remark != null ? remark.trim() : "审核通过，退款已执行")
        );
        if (affected != 1) {
            throw new BusinessException("退款申请状态已变化，审核失败");
        }

        // 条件更新成功后，再执行真正退款（复用 PaymentService 成熟逻辑）
        // 整个方法 @Transactional，如果 refundOrder 失败（订单/支付/库存异常），事务回滚，refund_order 状态也会回退到 10
        // 保证只有一个审核人能成功抢占并执行退款
        paymentService.refundOrder(admin, refund.getOrderId());

        log.info("退款申请审核通过，refundId={}, orderId={}, reviewer={}", refundId, refund.getOrderId(), admin.getUserId());
    }

    // ==================== 管理员：拒绝 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectRefund(LoginUser admin, Long refundId, String reason) {
        if (admin == null || admin.getRole() == null || (admin.getRole() != 1 && admin.getRole() != 2)) {
            throw new BusinessException(403, "仅管理员/卖家可审核退款申请");
        }
        if (!StringUtils.hasText(reason)) {
            throw new BusinessException("拒绝原因不能为空");
        }

        RefundOrder refund = refundOrderMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException("退款申请不存在");
        }
        if (refund.getStatus() != 10) {
            throw new BusinessException("只能拒绝状态为【待审核】的申请，当前状态：" + refund.getStatus());
        }

        // 仅更新申请状态，不触碰订单/库存（符合“申请与执行分离”）
        int affected = refundOrderMapper.update(null,
                new LambdaUpdateWrapper<RefundOrder>()
                        .eq(RefundOrder::getId, refundId)
                        .eq(RefundOrder::getStatus, 10)
                        .set(RefundOrder::getStatus, 30)
                        .set(RefundOrder::getReviewerId, admin.getUserId())
                        .set(RefundOrder::getReviewTime, LocalDateTime.now())
                        .set(RefundOrder::getReviewRemark, "拒绝理由：" + reason.trim())
        );
        if (affected != 1) {
            throw new BusinessException("退款申请状态已变化，操作失败");
        }

        log.info("退款申请已拒绝，refundId={}, orderId={}, reviewer={}", refundId, refund.getOrderId(), admin.getUserId());
    }

    // ==================== 私有辅助 ====================

    private RefundVO convertToVO(RefundOrder entity) {
        RefundVO vo = new RefundVO();
        BeanUtils.copyProperties(entity, vo);
        // 补充状态描述
        switch (entity.getStatus()) {
            case 10:
                vo.setStatusDesc("待审核");
                break;
            case 20:
                vo.setStatusDesc("已通过");
                break;
            case 30:
                vo.setStatusDesc("已拒绝");
                break;
            default:
                vo.setStatusDesc("未知");
        }
        return vo;
    }
}
