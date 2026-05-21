package com.mall.module.refund.service;

import com.mall.common.security.LoginUser;
import com.mall.module.refund.dto.RefundApplyDTO;
import com.mall.module.refund.vo.RefundVO;

import java.util.List;

/**
 * 退款申请与审核服务（Phase 11）
 */
public interface RefundService {

    /**
     * 买家提交退款申请
     *
     * @param buyer   当前登录买家
     * @param orderId 目标订单（必须是自己的 + status=20 + 无任何退款申请，本阶段一单一申请）
     * @param dto     包含退款原因
     * @return 创建的申请记录 ID
     */
    Long applyRefund(LoginUser buyer, Long orderId, RefundApplyDTO dto);

    /**
     * 买家查询自己的退款申请列表
     */
    List<RefundVO> listMyRefunds(LoginUser buyer);

    /**
     * 管理员查询所有待审核的退款申请
     */
    List<RefundVO> listPendingRefunds(LoginUser admin);

    /**
     * 管理员审核通过退款申请
     * 通过后调用支付服务执行实际退款（order→60, payment→40, 恢复库存）
     *
     * @param admin     审核人
     * @param refundId  申请ID
     * @param remark    可选通过备注
     */
    void approveRefund(LoginUser admin, Long refundId, String remark);

    /**
     * 管理员拒绝退款申请
     *
     * @param admin     审核人
     * @param refundId  申请ID
     * @param reason    拒绝原因（必填）
     */
    void rejectRefund(LoginUser admin, Long refundId, String reason);
}
