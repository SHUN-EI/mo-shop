package com.mo.component;

import com.mo.enums.OrderPayTypeEnum;
import com.mo.vo.PayInfoVO;
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
     * @param payInfoVO
     * @return
     */
    public String pay(PayInfoVO payInfoVO) {
        String payType = payInfoVO.getPayType();
        if (OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);
            return payStrategyContext.executeUnifiedorder(payInfoVO);
        } else if (OrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payType)) {
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeUnifiedorder(payInfoVO);
        }

        return null;
    }


    /**
     * 查询订单支付状态
     * 支付成功返回非空，其他返回空
     *
     * @param payInfoVO
     * @return
     */
    public String queryPaySuccess(PayInfoVO payInfoVO) {
        String payType = payInfoVO.getPayType();
        if (OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);
            return payStrategyContext.executeQueryPaySuccess(payInfoVO);
        } else if (OrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payType)) {
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeQueryPaySuccess(payInfoVO);
        }

        return null;
    }
}
