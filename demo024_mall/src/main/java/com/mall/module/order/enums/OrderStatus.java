package com.mall.module.order.enums;

import java.util.Arrays;

/**
 * 订单状态枚举（Phase 5 - demo023_mall 状态机核心）
 *
 * 设计要点：
 * - 状态码与数据库一致：10/20/30/40/50
 * - 提供 fromCode 工厂方法，避免魔法数字散落
 * - 封装状态流转规则（canCancel 等），集中管理而非散落在 Controller/Service 各处
 * - 终态判断：已完成/已取消不允许再变更
 *
 * 教学价值：
 * - 用枚举替代散落的 if/数字判断，提升可读性与可维护性
 * - 为未来完整状态机（支付、发货、完成）预留扩展点
 */
public enum OrderStatus {

    /** 10 - 待支付：初始状态，可取消 */
    PENDING_PAYMENT(10, "待支付"),

    /** 20 - 已支付：可进入发货流程（本阶段暂不实现） */
    PAID(20, "已支付"),

    /** 30 - 已发货：可进入完成（本阶段暂不实现） */
    SHIPPED(30, "已发货"),

    /** 40 - 已完成：终态 */
    COMPLETED(40, "已完成"),

    /** 50 - 已取消：终态，需回滚库存 */
    CANCELLED(50, "已取消");

    private final Integer code;
    private final String description;

    OrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据数据库 code 反查枚举（推荐使用）
     */
    public static OrderStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知的订单状态码: " + code));
    }

    /**
     * 判断当前状态是否允许取消订单
     * 规则：仅 10(待支付) 可取消
     */
    public boolean canCancel() {
        return this == PENDING_PAYMENT;
    }

    /**
     * 是否为终态（不允许再流转）
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == CANCELLED;
    }

    @Override
    public String toString() {
        return code + "(" + description + ")";
    }
}
