package com.mo.component;

import com.mo.vo.PayInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2021/5/4
 */
@Component
@Slf4j
public class WechatPayStrategy implements PayStrategy {

    @Override
    public String unifiedorder(PayInfoVo payInfoVo) {
        return null;
    }

    @Override
    public String refund(PayInfoVo payInfoVo) {
        return null;
    }

    @Override
    public String queryPaySuccess(PayInfoVo payInfoVo) {
        return null;
    }
}
