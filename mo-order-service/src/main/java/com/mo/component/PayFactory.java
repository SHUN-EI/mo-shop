package com.mo.component;

import com.mo.enums.OrderPayTypeEnum;
import com.mo.vo.PayInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2021/5/4
 */
@Component
public class PayFactory {

    @Autowired
    private AlipayStrategy alipayStrategy;
    @Autowired
    private WechatPayStrategy wechatPayStrategy;

    /**
     * 创建支付，简单工厂设计模式
     *
     * @param payInfoVo
     * @return
     */
    public String pay(PayInfoVo payInfoVo) {
        String payType = payInfoVo.getPayType();
        if (OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);
            return payStrategyContext.executeUnifiedorder(payInfoVo);
        } else if (OrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payType)) {
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeUnifiedorder(payInfoVo);
        }

        return null;
    }


    /**
     * 查询订单支付状态
     * 支付成功返回非空，其他返回空
     *
     * @param payInfoVo
     * @return
     */
    public String queryPaySuccess(PayInfoVo payInfoVo) {
        String payType = payInfoVo.getPayType();
        if (OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);
            return payStrategyContext.executeQueryPaySuccess(payInfoVo);
        } else if (OrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payType)) {
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeQueryPaySuccess(payInfoVo);
        }

        return null;
    }
}
