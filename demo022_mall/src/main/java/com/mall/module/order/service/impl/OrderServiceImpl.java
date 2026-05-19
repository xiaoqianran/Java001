package com.mall.module.order.service.impl;

import com.mall.common.exception.BusinessException;
import com.mall.module.cart.service.CartService;
import com.mall.module.order.dto.OrderCreateDTO;
import com.mall.module.order.dto.OrderItemDTO;
import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.order.service.OrderService;
import com.mall.module.product.sku.entity.Sku;
import com.mall.module.product.sku.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 订单服务实现（Phase 4 - demo022_mall 核心）
 *
 * 教学重点：
 * - 一个方法内完成「创建订单 + 扣库存 + 清购物车」的原子操作
 * - 正确使用现有 SkuService.reduceStock（乐观锁）
 * - 事务边界清晰
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SkuService skuService;
    private final CartService cartService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, OrderCreateDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        List<Long> skuIds = dto.getItems().stream()
                .map(OrderItemDTO::getSkuId)
                .collect(Collectors.toList());

        // 1. 批量查询 SKU（避免 N+1）
        List<Sku> skuList = skuService.listByIds(skuIds);
        if (skuList.size() != skuIds.size()) {
            throw new BusinessException("部分商品不存在");
        }

        // 2. 校验 + 扣减库存 + 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Sku sku = skuList.stream()
                    .filter(s -> s.getId().equals(itemDTO.getSkuId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("商品不存在"));

            if (sku.getStatus() != 1) {
                throw new BusinessException("商品已下架：" + sku.getSkuCode());
            }

            // 调用已有乐观锁扣库存方法（重要！）
            boolean success = skuService.reduceStock(sku.getId(), itemDTO.getQuantity());
            if (!success) {
                throw new BusinessException("库存不足或扣减失败，请重试");
            }

            // 累计金额
            BigDecimal itemTotal = sku.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // 构建订单项快照
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(sku.getId());
            orderItem.setSkuName(sku.getSkuCode()); // 简化，可后续关联 SPU 名称
            orderItem.setSkuSpecs(sku.getSpecs());
            orderItem.setPrice(sku.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItems.add(orderItem);
        }

        // 3. 生成订单号（简单实现，教学阶段够用）
        String orderNo = generateOrderNo(userId);

        // 4. 保存订单主表
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(10); // 待支付
        orderMapper.insert(order);

        // 5. 批量保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 6. 清空购物车中已下单的商品（同一事务内）
        cartService.removeCartItems(userId, skuIds);

        return order.getId();
    }

    /**
     * 简单订单号生成策略
     */
    private String generateOrderNo(Long userId) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(10000);
        return "ORD" + time + String.format("%04d", random) + userId % 10000;
    }
}