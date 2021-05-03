package com.mo.component;

import com.mo.vo.PayInfoVo;

/**
 * Created by mo on 2021/5/4
 */
public class PayStrategyContext {

    private PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }

    /**
     * 根据支付策略，调用不同的支付
     *
     * @param payInfoVo
     * @return
     */
    public String executeUnifiedorder(PayInfoVo payInfoVo) {
        return this.payStrategy.unifiedorder(payInfoVo);
    }

    /**
     * 根据支付的策略，调用不同的查询订单支持状态
     *
     * @param payInfoVo
     * @return
     */
    public String executeQueryPaySuccess(PayInfoVo payInfoVo) {
        return this.payStrategy.queryPaySuccess(payInfoVo);
    }
}
