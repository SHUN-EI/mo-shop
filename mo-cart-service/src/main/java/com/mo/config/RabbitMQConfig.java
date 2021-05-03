package com.mo.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mo on 2021/4/29
 */
@ConfigurationProperties(prefix = "mqconfig")
@Configuration
@Data
public class RabbitMQConfig {

    /**
     * 第一个队列:延迟队列，不能被监听消费
     */
    private String cartReleaseDelayQueue;
    /**
     * 第二个队列:被消费者监听恢复库存的队列
     * 延迟队列的消息过期后转发的队列-死信队列
     */
    private String cartReleaseQueue;
    /**
     * 交换机
     */
    private String cartEventExchange;
    /**
     * 第一个队列的路由key
     * 进入延迟队列的路由key
     */
    private String cartReleaseDelayRoutingKey;
    /**
     * 第二个队列的路由key
     * 消息过期，进入释放死信队列的key
     */
    private String cartReleaseRoutingKey;
    /**
     * 息过期时间,毫秒
     */
    private Integer ttl;

    /**
     * 消息转换器，把消息转为json格式
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建交换机 Topic类型，也可以用dirct路由
     * 一个微服务一个交换机
     *
     * @return
     */
    @Bean
    public Exchange cartEventchange() {
        return new TopicExchange(cartEventExchange, true, false);
    }

    /**
     * 延迟队列
     * 第一个队列:延迟队列，不能被监听消费
     *
     * @return
     */
    @Bean
    public Queue cartReleaseDelayQueue() {

        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-routing-key", cartReleaseRoutingKey);
        args.put("x-dead-letter-exchange", cartEventExchange);
        args.put("x-message-ttl", ttl);

        return new Queue(cartReleaseDelayQueue, true, false, false, args);
    }

    /**
     * 死信队列(使用普通队列)
     * 被消费者监听恢复库存的队列
     * 延迟队列的消息过期后转发的队列
     *
     * @return
     */
    @Bean
    public Queue cartReleaseQueue() {
        return new Queue(cartReleaseQueue, true, false, false);
    }

    /**
     * 第一个队列:延迟队列 与交换机 的绑定关系建立
     *
     * @return
     */
    @Bean
    public Binding cartReleaseDelayQueueBinding() {
        return new Binding(cartReleaseDelayQueue, Binding.DestinationType.QUEUE, cartEventExchange, cartReleaseDelayRoutingKey, null);
    }

    /**
     * 第二个队列:死信队列 与交换机 的绑定关系建立
     *
     * @return
     */
    @Bean
    public Binding cartReleaseQueueBinding() {
        return new Binding(cartReleaseQueue, Binding.DestinationType.QUEUE, cartEventExchange, cartReleaseRoutingKey, null);
    }

}