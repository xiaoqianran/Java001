package com.mall.module.cart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * =====================================================================
 * 【mall - Phase 3 Step 1】购物车实体（Cart）
 * =====================================================================
 *
 * 购物车在电商系统中的作用：
 * - 临时存放用户想要购买的商品
 * - 连接“浏览商品”和“生成订单”的桥梁
 * - 通常需要支持“未登录浏览 → 登录后合并购物车”（本项目先实现登录后购物车）
 *
 * 设计要点：
 * - user_id + sku_id 做唯一索引（一个用户对同一个SKU只能有一条记录）
 * - quantity 直接累加，而不是插入多条记录
 * - 购物车通常不存价格（价格以 SKU 实时价格为准，防止下单时价格变动）
 *
 * 教学重点：
 * - 为什么购物车要关联 SKU 而不是 SPU？
 * - 购物车与订单的关系（订单是购物车的快照）
 * =====================================================================
 */
@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** SKU ID */
    private Long skuId;

    /** 购买数量 */
    private Integer quantity;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}