package com.mo.biz;

import com.mo.OrderApplication;
import com.mo.enums.OrderCodeEnum;
import com.mo.utils.DateUtil;
import com.mo.utils.OrderCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * Created by mo on 2021/4/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
@Slf4j
public class OrderTest {

    @Autowired
    private OrderCodeGenerateUtil orderCodeGenerateUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public  void sendMessageTest(){
        rabbitTemplate.convertAndSend("order.event.exchange","order.close.delay.routing.key","this is order close message");
    }

    @Test
    public void OrderCodeGenerateTest() {
        String orderCode = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.XD);
        log.info("自动生成的订单号:{}", orderCode);
        //自动生成的订单号:XD210429000000005457
        //自动生成的订单号:XD210429000000018073
    }

    @Test
    public void DateTest() {

        String formate = "yyMMddhhmmss";
        String currentDate = DateUtil.formatCurrentDate(new Date(), formate);
        log.info("当前日期为:{}", currentDate);

    }
}
