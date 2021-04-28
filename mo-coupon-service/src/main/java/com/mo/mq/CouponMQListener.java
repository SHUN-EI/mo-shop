package com.mo.mq;

import com.mo.model.CouponRecordMessage;
import com.mo.service.CouponRecordService;
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
 * Created by mo on 2021/4/28
 */
@Component
@Slf4j
@RabbitListener(queues = "${mqconfig.coupon_release_queue}")
public class CouponMQListener {

    @Autowired
    private CouponRecordService couponRecordService;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 重复消息-幂等性
     * <p>
     * 消息消费失败，可以设置重新入队后最大的重试次数
     * <p>
     * 若消息消息失败，不重新入队，可以记录日志，然后插到数据库后续人工排查
     *
     * @param recordMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseCouponRecord(CouponRecordMessage recordMessage, Message message, Channel channel) throws IOException {

        log.info("监听到消息：releaseCouponRecord的消息内容:{}", recordMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = couponRecordService.releaseCouponRecord(recordMessage);

        //防止同个优惠券解锁任务并发进入，如果是串行消费消息，则不用加锁，加锁有利有弊，要看具体项目业务逻辑而定
        Lock lock = redissonClient.getLock("lock:coupon_record_release:" + recordMessage.getCouponTaskId());
        lock.lock();
        try {
            if (flag) {
                //确认消息消费成功
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("释放优惠券失败 flag=false,{}", recordMessage);
                //消息重新入队
                channel.basicReject(deliveryTag, true);
            }

        } catch (IOException e) {
            log.error("释放优惠券记录异常:{},msg:{}", e, recordMessage);
            //消息重新入队
            channel.basicReject(deliveryTag, true);
        } finally {
            lock.unlock();
        }
    }
}
