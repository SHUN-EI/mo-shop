package com.mo.mq;

import com.mo.model.CartMessage;
import com.mo.service.CartService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by mo on 2021/5/2
 */
@RabbitListener(queues = "${mqconfig.cart_release_queue}")
@Component
@Slf4j
public class CartMQListener {

    @Autowired
    private CartService cartService;

    @RabbitHandler
    public void recoverCartItems(CartMessage cartMessage, Message message, Channel channel) throws IOException {

        log.info("监听到消息：recoverCartItems的消息内容:{}", cartMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = cartService.recoverCartItems(cartMessage);

        try {
            if (flag) {
                //确认消息消费成功
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("恢复购物车里面的商品项目失败 flag=false,{}", cartMessage);
                //消息重新入队
                channel.basicReject(deliveryTag, true);
            }
        } catch (IOException e) {
            log.error("恢复购物车里面的商品项目异常:{},msg:{}", e, cartMessage);
            //消息重新入队
            channel.basicReject(deliveryTag, true);
        }
    }
}


