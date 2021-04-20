package com.mo.controller;

import com.google.code.kaptcha.Producer;
import com.mo.utils.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2021/4/19
 */
@Api(tags = "通知模块")
@RestController
@RequestMapping("/api/user/v1")
@Slf4j
public class NotifyController {

    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 图形验证码有效期为10分钟
     */
    private static final long KAPTCHA_CODE_EXPIRED = 60 * 1000 * 10;

    /**
     * 获取图形验证码
     *
     * @param request
     * @param response
     */
    @ApiOperation("获取图形验证码")
    @GetMapping("/getKaptchaCode")
    public void getKaptchaCode(HttpServletRequest request, HttpServletResponse response) {

        String cacheKey = getKaptchaKey(request);

        String kaptchaText = kaptchaProducer.createText();
        log.info("图形验证码:{}", kaptchaText);

        //存储
        redisTemplate.opsForValue().set(cacheKey,kaptchaText,KAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);

        BufferedImage image = kaptchaProducer.createImage(kaptchaText);
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            log.error("获取图形验证码异常:{}", e);
        }

    }

    /**
     * 获取缓存的key
     *
     * @param request
     * @return
     */
    private String getKaptchaKey(HttpServletRequest request) {
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");

        String key = "user-service:kaptcha:" + CommonUtil.MD5(ip + userAgent);
        log.info("ip={}", ip);
        log.info("UserAgent={}", userAgent);
        log.info("key={}", key);

        return key;
    }

}
