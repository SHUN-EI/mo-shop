package com.mo.component;

import com.mo.vo.PayInfoVo;

/**
 * Created by mo on 2021/5/4
 */
public interface PayStrategy {

    /**
     * 下单
     *
     * @param payInfoVo
     * @return
     */
    String unifiedorder(PayInfoVo payInfoVo);

    /**
     * 退款
     *
     * @param payInfoVo
     * @return
     */
    default String refund(PayInfoVo payInfoVo) {
        return "";
    }

    /**
     * 查询支付是否成功
     *
     * @param payInfoVo
     * @return
     */
    default String queryPaySuccess(PayInfoVo payInfoVo) {
        return "";
    }

}
