package com.mall.module.order.enums;

import com.mall.common.exception.BusinessException;
import java.util.Arrays;

/**
 * 订单状态枚举（Phase 7 - demo025_mall 发货完成核心）
 *
 * 设计要点：
 * - 状态码与数据库一致：10/20/30/40/50
 * - 提供 fromCode 工厂方法（非法 code 抛 BusinessException）
 * - 封装状态流转规则（canCancel、canPay、canShip、canComplete、isTerminal）
 *
 * 教学价值：
 * - 状态机 + 权限结合
 * - 完整买家端订单生命周期
 */
public enum OrderStatus {

    /** 10 - 待支付：初始状态，可取消 */
    PENDING_PAYMENT(10, "待支付"),

    /** 20 - 已支付：可发货 */
    PAID(20, "已支付"),

    /** 30 - 已发货：可完成 */
    SHIPPED(30, "已发货"),

    /** 40 - 已完成：终态 */
    COMPLETED(40, "已完成"),

    /** 50 - 已取消：终态，需回滚库存 */
    CANCELLED(50, "已取消"),

    /** 60 - 已退款：终态（Phase 10 新增），已支付未发货订单退款后状态 */
    REFUNDED(60, "已退款");

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
     * 非法 code 抛 BusinessException，便于上层统一异常处理
     */
    public static OrderStatus fromCode(Integer code) {
        if (code == null) {
            throw new BusinessException("订单状态码不能为空");
        }
        return Arrays.stream(values())
                .filter(status -> status.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException("未知的订单状态码: " + code));
    }

    /**
     * 判断当前状态是否允许取消订单
     * 规则：仅 10(待支付) 可取消
     */
    public boolean canCancel() {
        return this == PENDING_PAYMENT;
    }

    /**
     * 判断当前状态是否允许支付
     * 规则：仅 10(待支付) 可支付（Phase 6 新增）
     */
    public boolean canPay() {
        return this == PENDING_PAYMENT;
    }

    /**
     * 判断当前状态是否允许发货
     * 规则：仅 20(已支付) 可发货（Phase 7 新增）
     */
    public boolean canShip() {
        return this == PAID;
    }

    /**
     * 判断当前状态是否允许完成
     * 规则：仅 30(已发货) 可完成（Phase 7 新增）
     */
    public boolean canComplete() {
        return this == SHIPPED;
    }

    /**
     * 判断当前状态是否允许退款（Phase 10 新增）
     * 规则：仅 20(已支付) 可退款
     * 禁止：待支付(10)、已发货(30+)、已取消(50)、已退款(60)
     */
    public boolean canRefund() {
        return this == PAID;
    }

    /**
     * 是否为终态（不允许再流转）
     * 别名 isTerminal，便于后续代码阅读
     */
    public boolean isFinalState() {
        return isTerminal();
    }

    /**
     * 是否为终态（不允许再流转）
     * Phase 6/7/10 明确定义：COMPLETED、CANCELLED、REFUNDED 是终态
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == REFUNDED;
    }

    @Override
    public String toString() {
        return code + "(" + description + ")";
    }
}
