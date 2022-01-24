package com.mo.mq;

import com.mo.model.OrderMessage;
import com.mo.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by mo on 2021/5/3
 */
@RabbitListener(queues = "${mqconfig.order_close_queue}")
@Component
@Slf4j
public class OrderMQListener {

    @Autowired
    private OrderService orderService;

    /**
     * 重复消息-幂等性
     * <p>
     * 消息消费失败，可以设置重新入队后最大的重试次数
     * <p>
     * 若消息消息失败，不重新入队，可以记录日志，然后插到数据库后续人工排查
     *
     * @param orderMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void closeOrder(OrderMessage orderMessage, Message message, Channel channel) throws IOException {

        log.info("监听到消息:closeOrder的消息内容;{}", orderMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = orderService.closeOrder(orderMessage);

        try {

            if (flag) {
                //确认消息消费成功
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("关闭订单失败 flag=false,{}", orderMessage);
                //消息重新入队
                channel.basicReject(deliveryTag, true);
            }
        } catch (IOException e) {
            log.error("关闭订单异常:{},msg:{}", e, orderMessage);
            //消息重新入队
            channel.basicReject(deliveryTag, true);
        }
    }
}
