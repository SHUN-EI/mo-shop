package com.mo.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.OrderCodeEnum;
import com.mo.exception.BizException;
import com.mo.feign.UserFeignService;
import com.mo.interceptor.LoginInterceptor;
import com.mo.mapper.MpOrderMapper;
import com.mo.model.LoginUserDTO;
import com.mo.model.MpOrderDO;
import com.mo.request.CreateOrderRequest;
import com.mo.service.OrderService;
import com.mo.utils.JsonData;
import com.mo.utils.OrderCodeGenerateUtil;
import com.mo.vo.AddressVO;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mo
 * @since 2021-04-26
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MpOrderMapper orderMapper;
    @Autowired
    private OrderCodeGenerateUtil orderCodeGenerateUtil;
    @Autowired
    private UserFeignService userFeignService;

    @Override
    public JsonData createOrder(CreateOrderRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        //生成订单号
        String outTradeNo = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.XD);
        //防重提交

        //用户微服务-确认收货地址，防止越权
        AddressVO addressVO = getUserAddress(request.getAddressId());

        //商品微服务-获取最新购物车商品项目和价格

        //订单验价，后端需要计算校验订单价格，不能单以前端为准
        //优惠券微服务-获取优惠券
        //验证价格

        //锁定优惠券

        //锁定商品库存

        //创建订单对象

        //创建订单详情对象

        //发送延迟消息-用于自动关单

        //创建支付信息-对接第三方支付

        return null;
    }


    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public JsonData queryOrderState(String outTradeNo) {

        MpOrderDO orderDO = orderMapper.selectOne(new QueryWrapper<MpOrderDO>().eq("out_trade_no", outTradeNo));

        if (null == orderDO) {
            return null;
        } else {
            return JsonData.buildSuccess(orderDO);
        }
    }

    /**
     * 获取用户收货地址
     *
     * @param addressId
     * @return
     */
    private AddressVO getUserAddress(Long addressId) {
        JsonData data = userFeignService.detail(addressId);

        if (data.getCode() != 0) {
            log.error("获取收货地址失败,msg:{}", data);
            throw new BizException(BizCodeEnum.ADDRESS_NOT_EXIST);
        }

        AddressVO addressVO = data.getData(new TypeReference<>() {
        });

        return addressVO;
    }
}
