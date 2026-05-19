package com.mall.module.payment.service.impl;

import com.mall.common.exception.BusinessException;
import com.mall.module.order.entity.Order;
import com.mall.module.order.enums.OrderStatus;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.payment.dto.MockPaymentCallbackDTO;
import com.mall.module.payment.entity.PaymentOrder;
import com.mall.module.payment.enums.PaymentStatus;
import com.mall.module.payment.mapper.PaymentOrderMapper;
import com.mall.module.payment.service.PaymentService;
import com.mall.module.payment.vo.PaymentOrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 支付服务实现（Phase 9）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final OrderMapper orderMapper;

    @Value("${mall.payment.mock-callback-secret:demo027}")
    private String mockCallbackSecret;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrderVO createPaymentForOrder(Long userId, Long orderId) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(403, "订单不存在或无权操作");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能为自己的订单创建支付单");
        }

        // 2. 状态校验
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException("只有待支付订单可以创建支付单，当前状态：" + order.getStatus());
        }

        // 3. 检查是否已有支付单
        PaymentOrder existing = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getOrderId, orderId)
        );

        if (existing != null) {
            if (existing.getStatus() == PaymentStatus.SUCCESS.getCode()) {
                throw new BusinessException("订单已支付");
            }
            // 待支付或失败，直接返回已有支付单（幂等）
            return convertToVO(existing);
        }

        // 4. 创建新支付单（并发幂等保护）
        PaymentOrder payment = new PaymentOrder();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(orderId);
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(userId);
        payment.setAmount(order.getTotalAmount());
        payment.setChannel("MOCK");
        payment.setStatus(PaymentStatus.PENDING.getCode());

        try {
            paymentOrderMapper.insert(payment);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 并发创建时，另一个请求已经成功插入，重新查询返回
            PaymentOrder existingPayment = paymentOrderMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaymentOrder>()
                            .eq(PaymentOrder::getOrderId, orderId)
            );
            if (existingPayment != null) {
                return convertToVO(existingPayment);
            }
            throw new BusinessException("创建支付单失败，请重试");
        }

        return convertToVO(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleMockCallback(MockPaymentCallbackDTO dto) {
        log.info("收到模拟支付回调, paymentNo={}, orderId={}, payStatus={}", 
                dto.getPaymentNo(), dto.getOrderId(), dto.getPayStatus());

        // 1. 校验 mockSign
        if (!mockCallbackSecret.equals(dto.getMockSign())) {
            throw new BusinessException(403, "非法支付回调");
        }

        // 2. 查询支付单
        PaymentOrder payment = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getPaymentNo, dto.getPaymentNo())
        );
        if (payment == null) {
            throw new BusinessException("支付单不存在");
        }

        // 3. 校验金额
        if (dto.getPaidAmount() == null || dto.getPaidAmount().compareTo(payment.getAmount()) != 0) {
            // 金额不匹配，直接失败，不落库（由外层事务决定是否记录）
            throw new BusinessException("支付金额不匹配");
        }

        // 4. 幂等判断
        if (payment.getStatus() == PaymentStatus.SUCCESS.getCode()) {
            // 已经成功，直接返回（幂等）
            log.info("重复支付回调，已忽略, paymentNo={}", dto.getPaymentNo());
            return;
        }

        // 5. 查询订单
        Order order = orderMapper.selectById(payment.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 6. 订单状态校验
        if (order.getStatus() == OrderStatus.CANCELLED.getCode()) {
            // 订单已取消，直接失败，不落库
            throw new BusinessException("订单已取消，不能支付");
        }

        if (dto.getPayStatus() != null && "SUCCESS".equalsIgnoreCase(dto.getPayStatus())) {
            // 7. 支付成功流程

            // 支付单 10 → 20
            payment.setStatus(PaymentStatus.SUCCESS.getCode());
            payment.setCallbackTime(LocalDateTime.now());
            int payUpdate = paymentOrderMapper.updateById(payment);
            if (payUpdate != 1) {
                throw new BusinessException("支付单更新失败");
            }

            // 订单 10 → 20（条件更新）
            int orderAffected = orderMapper.update(null,
                    new LambdaUpdateWrapper<Order>()
                            .eq(Order::getId, order.getId())
                            .eq(Order::getStatus, OrderStatus.PENDING_PAYMENT.getCode())
                            .set(Order::getStatus, OrderStatus.PAID.getCode())
            );

            if (orderAffected != 1) {
                // 可能是重复回调成功，或订单已被其他流程修改
                Order latestOrder = orderMapper.selectById(order.getId());
                if (latestOrder != null && latestOrder.getStatus() == OrderStatus.PAID.getCode()) {
                    log.info("订单已支付，重复回调已忽略, orderId={}", order.getId());
                    return;
                }
                throw new BusinessException("订单状态已变化，不能支付");
            }

            log.info("支付回调处理成功, paymentNo={}, orderId={}", dto.getPaymentNo(), order.getId());

        } else {
            // 支付失败（payStatus != SUCCESS）
            // 本阶段重点处理 SUCCESS 回调，失败回调仅记录日志，不做复杂业务处理
            log.info("支付回调为失败状态, paymentNo={}, payStatus={}", dto.getPaymentNo(), dto.getPayStatus());
        }
    }

    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private PaymentOrderVO convertToVO(PaymentOrder payment) {
        PaymentOrderVO vo = new PaymentOrderVO();
        BeanUtils.copyProperties(payment, vo);
        return vo;
    }
}
