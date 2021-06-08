package com.mo.mq;

import com.mo.model.CartMessage;
import com.mo.service.CartService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

/**
 * Created by mo on 2021/5/2
 */
@RabbitListener(queues = "${mqconfig.cart_release_queue}")
@Component
@Slf4j
public class CartMQListener {

    @Autowired
    private CartService cartService;
    @Autowired
    private RedissonClient redissonClient;

    @RabbitHandler
    public void recoverCartItems(CartMessage cartMessage, Message message, Channel channel) throws IOException {

        //防止同个购物项目恢复任务并发进入，如果是串行消费消息，则不用加锁，加锁有利有弊，要看具体项目业务逻辑而定
        Lock lock = redissonClient.getLock("lock:cart_items_recover:" + cartMessage.getCartTaskId());
        lock.lock();

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
        } finally {
            lock.unlock();
        }
    }
}


