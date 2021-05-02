package com.mo.biz;

import com.mo.CartApplication;
import com.mo.model.CartMessage;
import com.mo.model.ProductMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;

/**
 * Created by mo on 2021/5/2
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CartApplication.class)
@Slf4j
public class CartTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage() {
        rabbitTemplate.convertAndSend("cart.event.exchange", "cart.release.delay.routing.key", "this is cart items lock");
    }

    @Test
    public void cartItemsRecoverTest() {
        CartMessage message = new CartMessage();
        message.setOutTradeNo("XD210429000000018073");
        message.setCartTaskId(1L);
        rabbitTemplate.convertAndSend("cart.event.exchange", "cart.release.delay.routing.key", message);
    }

}
