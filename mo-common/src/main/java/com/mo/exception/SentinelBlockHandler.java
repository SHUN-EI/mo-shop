package com.mo.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.mo.enums.BizCodeEnum;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by mo on 2021/5/6
 * Sentinel 异常处理
 */
@Component
public class SentinelBlockHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {

        JsonData jsonData = null;

        if (e instanceof FlowException) {
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_FLOW);
        } else if (e instanceof DegradeException) {
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_DEGRADE);
        } else if (e instanceof AuthorityException) {
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_AUTH);
        }
        response.setStatus(200);
        CommonUtil.sendJsonMessage(response, jsonData);
    }
}
