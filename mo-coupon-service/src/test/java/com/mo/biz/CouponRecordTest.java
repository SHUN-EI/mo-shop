package com.mo.biz;

import com.mo.CouponApplication;
import com.mo.model.CouponRecordMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mo on 2021/4/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouponApplication.class)
@Slf4j
public class CouponRecordTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage() {
        rabbitTemplate.convertAndSend("coupon.event.exchange", "coupon.release.delay.routing.key", "this is coupon record lock");
    }

    @Test
    public void couponRecordReleaseTest() {
        CouponRecordMessage message = new CouponRecordMessage();
        message.setOutTradeNo("XD210429000000018073");
        message.setCouponTaskId(1L);

        rabbitTemplate.convertAndSend("coupon.event.exchange", "coupon.release.delay.routing.key", message);
    }
}
