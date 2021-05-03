package com.mo.component;

import com.mo.vo.PayInfoVO;

/**
 * Created by mo on 2021/5/4
 */
public interface PayStrategy {

    /**
     * 下单
     *
     * @param payInfoVO
     * @return
     */
    String unifiedorder(PayInfoVO payInfoVO);

    /**
     * 退款
     *
     * @param payInfoVO
     * @return
     */
    default String refund(PayInfoVO payInfoVO) {
        return "";
    }

    /**
     * 查询支付是否成功
     *
     * @param payInfoVO
     * @return
     */
    default String queryPaySuccess(PayInfoVO payInfoVO) {
        return "";
    }

}
