package com.mall.module.payment.enums;

/**
 * 支付单状态枚举（Phase 9）
 */
public enum PaymentStatus {
    PENDING(10, "待支付"),
    SUCCESS(20, "支付成功"),
    FAILED(30, "支付失败");

    private final Integer code;
    private final String desc;

    PaymentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PaymentStatus fromCode(Integer code) {
        if (code == null) return null;
        for (PaymentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知支付状态: " + code);
    }
}
