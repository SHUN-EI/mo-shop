package com.mo.mq;

import com.mo.model.ProductMessage;
import com.mo.service.ProductService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

/**
 * Created by mo on 2021/4/30
 */
@RabbitListener(queues = "${mqconfig.product_release_queue}")
@Component
@Slf4j
public class ProductMQListener {

    @Autowired
    private ProductService productService;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 重复消息-幂等性
     * <p>
     * 消息消费失败，可以设置重新入队后最大的重试次数
     * <p>
     * 若消息消息失败，不重新入队，可以记录日志，然后插到数据库后续人工排查
     *
     * @param productMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseProductStock(ProductMessage productMessage, Message message, Channel channel) throws IOException {

        //防止同个商品库存解锁任务并发进入，如果是串行消费消息，则不用加锁，加锁有利有弊，要看具体项目业务逻辑而定
        Lock lock = redissonClient.getLock("lock:product_stock_release" + productMessage.getProductTaskId());
        lock.lock();

        log.info("监听到消息：releaseProductStock的消息内容:{}", productMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = productService.releaseProductStock(productMessage);

        try {
            if (flag) {
                //确认消息消费成功
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("释放商品库存失败 flag=false,{}", productMessage);
                //消息重新入队
                channel.basicReject(deliveryTag, true);
            }
        } catch (IOException e) {
            log.error("释放商品库存记录异常:{},msg:{}", e, productMessage);
            //消息重新入队
            channel.basicReject(deliveryTag, true);
        } finally {
            lock.unlock();
        }
    }
}
