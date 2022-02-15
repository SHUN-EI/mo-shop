package com.mo.service.impl;

import com.mo.component.MailService;
import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.service.NotifyService;
import com.mo.utils.CheckUtil;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2021/4/20
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private MailService mailService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 验证码的标题
     */
    private static final String SUBJECT = "MoShop验证码";

    /**
     * 验证码的内容
     */
    private static final String CONTENT = "您的验证码是%s,有效时间是5分钟,打死都不要告诉别人哦";

    /**
     * 验证码过期时间，10min有效
     */
    private static final int CODE_EXPIRED = 60 * 1000 * 5;

    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {

        String cachekey = String.format(CacheKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = redisTemplate.opsForValue().get(cachekey);

        //当前时间戳
        Long currentTimestamp = CommonUtil.getCurrentTimestamp();

        //如果cacheValue 不为空，则判断是否60s内重复发送
        if (StringUtils.isNoneBlank(cacheValue)) {
            long ttl = Long.parseLong(cacheValue.split("_")[1]);

            //当前时间戳-验证码发送时间戳，如果小于60秒，则不给重复发送
            if (currentTimestamp - ttl < 1000 * 60) {
                log.info("重复发送验证码,时间间隔:{} 秒", (currentTimestamp - ttl) / 1000);
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }

        //邮箱验证码
        String code = CommonUtil.getRandomCode(6);
        //拼接验证码 eg:8868_3273767673367
        String value = code + "_" + currentTimestamp;
        redisTemplate.opsForValue().set(cachekey, value, CODE_EXPIRED, TimeUnit.MILLISECONDS);

        if (CheckUtil.isEmail(to)) {
            //发送邮箱验证码
            mailService.sendMail(to, SUBJECT, String.format(CONTENT, code));
            return JsonData.buildSuccess();

        } else if (CheckUtil.isPhone(to)) {
            //TODO 短信验证码

        }
        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }

    @Override
    public Boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {

        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);

        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNoneBlank(cacheValue)) {

            String cacheCode = cacheValue.split("_")[0];
            //校验验证码
            if (cacheCode.equals(code)) {
                //删除缓存中的验证码
                redisTemplate.delete(cacheKey);
                return true;
            }
        }

        return false;
    }
}
