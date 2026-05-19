package com.mall.module.product.sku.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * =====================================================================
 * 【mall - Phase 2 Step 4】SKU 实体（Stock Keeping Unit）
 * =====================================================================
 *
 * 什么是 SKU？
 * SKU = Stock Keeping Unit（库存量单位），是商品的**最小库存单元**。
 *
 * SPU vs SKU 经典例子：
 *   SPU: iPhone 16 Pro（一个商品）
 *   SKU:
 *     - iPhone 16 Pro 128G 黑色 → 售价 7999，库存 120
 *     - iPhone 16 Pro 256G 白色 → 售价 8999，库存 85
 *
 * 教学重点：
 * - 一个 SPU 对应多个 SKU
 * - SKU 才是真正下单、扣库存、定价的最小单位
 * - specs 字段使用 JSON 是为了教学初期简化（避免过早引入属性表）
 *
 * 后续演进方向：
 * - 将 specs 拆分为独立的 product_attribute + sku_spec 表（更规范）
 * - 支持 SKU 图片、重量、条形码等字段
 * =====================================================================
 */
@Data
@TableName("product_sku")
public class Sku {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的 SPU */
    private Long spuId;

    /** SKU 唯一编码 */
    private String skuCode;

    /** 售价 */
    private BigDecimal price;

    /** 库存 */
    private Integer stock;

    /**
     * 乐观锁版本号
     * MyBatis-Plus 会自动在更新时 +1，用于防止并发扣库存时的数据不一致
     */
    @Version
    private Integer version;

    /**
     * 规格属性（JSON 格式）
     * 示例：{"颜色": "黑色", "内存": "128G"}
     */
    private String specs;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}