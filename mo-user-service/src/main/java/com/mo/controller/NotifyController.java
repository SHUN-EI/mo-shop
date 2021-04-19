package com.mo.controller;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 获取图形验证码
     * @param request
     * @param response
     */
    @ApiOperation("获取图形验证码")
    @GetMapping("/getKaptchaCode")
    public void getKaptchaCode(HttpServletRequest request, HttpServletResponse response) {

        String kaptchaText = kaptchaProducer.createText();
        log.info("图形验证码:{}", kaptchaText);

        BufferedImage image = kaptchaProducer.createImage(kaptchaText);
        ServletOutputStream outputStream = null;
        try {
            outputStream=response.getOutputStream();
            ImageIO.write(image,"jpg",outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            log.error("获取图形验证码异常:{}", e);
        }

    }
}
