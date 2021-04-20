package com.mo.service.impl;

import com.mo.component.MailService;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.service.NotifyService;
import com.mo.utils.CheckUtil;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mo on 2021/4/20
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private MailService mailService;

    /**
     * 验证码的标题
     */
    private static final String SUBJECT = "MoShop验证码";

    /**
     * 验证码的内容
     */
    private static final String CONTENT = "您的验证码是%s,有效时间是60s,打死都不要告诉别人哦";

    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {

        if (CheckUtil.isEmail(to)) {
            //邮箱验证码
            String code = CommonUtil.getRandomCode(6);
            mailService.sendMail(to, SUBJECT, String.format(CONTENT,code));
            return JsonData.buildSuccess();

        } else if (CheckUtil.isPhone(to)) {
            //TODO 短信验证码

        }
        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }
}
