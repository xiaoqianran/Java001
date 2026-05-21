package com.mall.module.payment.service.impl;

import com.mall.common.exception.BusinessException;
import com.mall.common.security.LoginUser;
import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.enums.OrderStatus;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.payment.dto.MockPaymentCallbackDTO;
import com.mall.module.payment.entity.PaymentOrder;
import com.mall.module.payment.enums.PaymentStatus;
import com.mall.module.payment.mapper.PaymentOrderMapper;
import com.mall.module.payment.service.PaymentService;
import com.mall.module.payment.vo.PaymentOrderVO;
import com.mall.module.product.sku.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付服务实现（Phase 9）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SkuService skuService;

    @Value("${mall.payment.mock-callback-secret:demo028}")
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

    // ==================== Phase 10: 模拟退款（20 → 60，已支付未发货订单，Phase 11 限制仅 ADMIN/SELLER 或内部调用）====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(LoginUser operator, Long orderId) {
        // 历史接口默认全额退款（Phase 12 保留）
        // 先查询支付单金额，再调用带金额的重载
        PaymentOrder payment = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderId, orderId)
        );
        if (payment == null) {
            throw new BusinessException("支付单不存在");
        }
        refundOrder(operator, orderId, payment.getAmount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(LoginUser operator, Long orderId, java.math.BigDecimal refundAmount) {
        if (operator == null || operator.getUserId() == null || operator.getRole() == null) {
            throw new BusinessException(403, "无权操作退款");
        }

        int role = operator.getRole();

        // 禁止 BUYER 绕过审核
        if (role == 3) {
            throw new BusinessException(403, "买家请先提交退款申请，不能直接退款");
        }
        if (role != 1 && role != 2) {
            throw new BusinessException(403, "无权操作退款");
        }

        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(403, "订单不存在或无权操作");
        }

        // 2. 订单状态校验：仅 20 已支付可退款
        if (!OrderStatus.fromCode(order.getStatus()).canRefund()) {
            throw new BusinessException("只有已支付(未发货)订单可以退款，当前状态：" + order.getStatus());
        }

        // 3. 查询支付单
        PaymentOrder payment = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getOrderId, orderId)
        );
        if (payment == null) {
            throw new BusinessException("该订单没有支付单，无法退款");
        }
        if (payment.getStatus() != PaymentStatus.SUCCESS.getCode()) {
            throw new BusinessException("只有支付成功的支付单可以退款，当前支付单状态：" + payment.getStatus());
        }

        // 4. 校验传入的退款金额（Phase 12）
        if (refundAmount == null || refundAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("退款金额必须大于0");
        }
        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new BusinessException("退款金额不能超过支付金额");
        }

        // 5. 原子条件更新：订单 20 → 60
        int orderAffected = orderMapper.update(null,
                new LambdaUpdateWrapper<Order>()
                        .eq(Order::getId, orderId)
                        .eq(Order::getStatus, OrderStatus.PAID.getCode())
                        .set(Order::getStatus, OrderStatus.REFUNDED.getCode())
        );
        if (orderAffected != 1) {
            throw new BusinessException("订单状态已变化，不能退款");
        }

        // 6. 原子条件更新：支付单 20 → 40 + 写入 refunded_amount（Phase 12 核心）
        int paymentAffected = paymentOrderMapper.update(null,
                new LambdaUpdateWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getOrderId, orderId)
                        .eq(PaymentOrder::getStatus, PaymentStatus.SUCCESS.getCode())
                        .set(PaymentOrder::getStatus, PaymentStatus.REFUNDED.getCode())
                        .set(PaymentOrder::getRefundedAmount, refundAmount)
                        .set(PaymentOrder::getCallbackTime, LocalDateTime.now())
        );
        if (paymentAffected != 1) {
            throw new BusinessException("支付单状态已变化，不能退款");
        }

        // 6. 恢复库存（退款 = 交易取消）
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        );
        if (items.isEmpty()) {
            throw new BusinessException("订单明细为空，无法安全退款");
        }
        for (OrderItem item : items) {
            skuService.restoreStock(item.getSkuId(), item.getQuantity());
            // restoreStock 失败会抛异常 → 整个 @Transactional 回滚
        }

        log.info("退款成功，orderId={}, userId={}, operatorRole={}", orderId, order.getUserId(), role);
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
