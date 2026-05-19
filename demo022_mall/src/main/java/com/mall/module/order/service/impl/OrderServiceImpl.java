package com.mall.module.order.service.impl;

import com.mall.common.exception.BusinessException;
import com.mall.module.cart.service.CartService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.module.order.dto.OrderCreateDTO;
import com.mall.module.order.dto.OrderItemDTO;
import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.order.service.OrderService;
import com.mall.module.order.vo.OrderItemVO;
import com.mall.module.order.vo.OrderVO;
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

        // 1. 先合并同一 SKU 的数量（避免重复下单同一商品导致误判）
        java.util.Map<Long, Integer> skuQuantityMap = new java.util.HashMap<>();
        for (OrderItemDTO item : dto.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() < 1) {
                throw new BusinessException("商品数量必须大于0");
            }
            skuQuantityMap.merge(item.getSkuId(), item.getQuantity(), Integer::sum);
        }

        List<Long> mergedSkuIds = new ArrayList<>(skuQuantityMap.keySet());

        // 2. 批量查询 SKU
        List<Sku> skuList = skuService.listByIds(mergedSkuIds);
        if (skuList.size() != mergedSkuIds.size()) {
            throw new BusinessException("部分商品不存在或已下架");
        }

        // 3. 校验 + 扣减库存 + 构建快照 + 计算金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Sku sku : skuList) {
            Integer quantity = skuQuantityMap.get(sku.getId());

            if (sku.getStatus() != 1) {
                throw new BusinessException("商品已下架：" + sku.getSkuCode());
            }

            boolean deductSuccess = skuService.reduceStock(sku.getId(), quantity);
            if (!deductSuccess) {
                throw new BusinessException("库存不足，商品：" + sku.getSkuCode());
            }

            BigDecimal itemTotal = sku.getPrice().multiply(new BigDecimal(quantity));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(sku.getId());
            orderItem.setSkuName(sku.getSkuCode());
            orderItem.setSkuSpecs(sku.getSpecs() != null ? sku.getSpecs() : "{}");
            orderItem.setPrice(sku.getPrice());
            orderItem.setQuantity(quantity);
            orderItems.add(orderItem);
        }

        // 4. 生成订单号（带重试防重复）
        String orderNo = generateOrderNoWithRetry(userId);

        // 5. 保存订单主表
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(10);
        int orderInsert = orderMapper.insert(order);
        if (orderInsert <= 0) {
            throw new BusinessException("创建订单失败");
        }

        // 6. 保存订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            int itemInsert = orderItemMapper.insert(item);
            if (itemInsert <= 0) {
                throw new BusinessException("保存订单明细失败");
            }
        }

        // 7. 清空购物车（同一事务）
        boolean cartClear = cartService.removeCartItems(userId, mergedSkuIds);
        if (!cartClear) {
            // 购物车清空失败不一定致命，但按严格一致性仍抛出
            throw new BusinessException("清空购物车失败");
        }

        return order.getId();
    }

    private String generateOrderNoWithRetry(Long userId) {
        for (int i = 0; i < 3; i++) {
            String orderNo = generateOrderNo(userId);
            // 简单判断是否已存在（生产环境可用唯一索引 + 捕获 DuplicateKeyException）
            Long count = orderMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                            .eq(Order::getOrderNo, orderNo)
            );
            if (count == 0) {
                return orderNo;
            }
        }
        throw new BusinessException("生成订单号失败，请稍后重试");
    }

    /**
     * 简单订单号生成策略
     */
    private String generateOrderNo(Long userId) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(10000);
        return "ORD" + time + String.format("%04d", random) + userId % 10000;
    }

    @Override
    public Page<OrderVO> listUserOrders(Long userId, int current, int size) {
        // 1. 查询当前用户的订单（分页）
        var orderPage = orderMapper.selectPage(
                new Page<>(current, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime)
        );

        if (orderPage.getRecords().isEmpty()) {
            return new Page<>();
        }

        // 2. 批量查询所有订单的明细
        List<Long> orderIds = orderPage.getRecords().stream().map(Order::getId).collect(Collectors.toList());
        List<OrderItem> allItems = orderItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getOrderId, orderIds)
        );

        // 3. 按 orderId 分组
        java.util.Map<Long, List<OrderItem>> itemsMap = allItems.stream()
                .collect(java.util.stream.Collectors.groupingBy(OrderItem::getOrderId));

        // 4. 组装 VO
        List<OrderVO> voList = orderPage.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setStatus(order.getStatus());
            vo.setCreateTime(order.getCreateTime());

            List<OrderItem> items = itemsMap.getOrDefault(order.getId(), java.util.Collections.emptyList());
            vo.setItems(items.stream().map(this::convertToItemVO).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());

        Page<OrderVO> resultPage = new Page<>();
        resultPage.setRecords(voList);
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setCurrent(orderPage.getCurrent());
        resultPage.setSize(orderPage.getSize());
        return resultPage;
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .eq(Order::getId, orderId)
                        .eq(Order::getUserId, userId)
        );
        if (order == null) {
            throw new BusinessException("订单不存在或无权限查看");
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        );

        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items.stream().map(this::convertToItemVO).collect(Collectors.toList()));

        return vo;
    }

    private OrderItemVO convertToItemVO(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setSkuId(item.getSkuId());
        vo.setSkuName(item.getSkuName());
        vo.setSkuSpecs(item.getSkuSpecs());
        vo.setPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        return vo;
    }
}