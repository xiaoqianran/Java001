package com.mall.module.product.spu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 3】SPU 实体（Standard Product Unit）
 * =====================================================================
 *
 * 什么是 SPU？
 * SPU = Standard Product Unit（标准产品单元），是商品信息聚合的最小单位。
 * 它描述的是“一个商品”本身，而不是具体规格。
 *
 * 例子：
 * - iPhone 16 Pro（这是一个 SPU）
 *   - 它下面会有很多 SKU：
 *     - 128G 黑色
 *     - 256G 白色
 *     - ...
 *
 * 教学重点：
 * - SPU vs SKU 的区别（这是电商最核心的概念之一）
 * - SPU 主要存“公共信息”（名称、描述、品牌、分类）
 * - SKU 存“差异化信息”（规格、价格、库存、图片）
 *
 * 当前阶段我们先实现 SPU，下一阶段再叠加 SKU。
 * =====================================================================
 */
@Data
@TableName("product_spu")
public class Spu {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属分类 */
    private Long categoryId;

    /** 商品名称 */
    private String name;

    /** 商品描述 */
    private String description;

    /** 品牌 */
    private String brand;

    /** 状态：0=下架，1=上架 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}