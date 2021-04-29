package com.mo.biz;

import com.mo.ProductApplication;
import com.mo.model.CouponRecordMessage;
import com.mo.model.ProductMessage;
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
@SpringBootTest(classes = ProductApplication.class)
@Slf4j
public class ProductTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage() {
        rabbitTemplate.convertAndSend("product.event.exchange", "product.release.delay.routing.key", "this is product stock  lock");
    }

    @Test
    public void productStockReleaseTest() {
        ProductMessage message = new ProductMessage();
        message.setOutTradeNo("XD210429000000018073");
        message.setProductTaskId(1L);
        rabbitTemplate.convertAndSend("product.event.exchange", "product.release.delay.routing.key",message);
    }

}
