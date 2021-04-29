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
    private String productReleaseDelayQueue;
    /**
     * 第二个队列:被消费者监听恢复库存的队列
     * 延迟队列的消息过期后转发的队列-死信队列
     */
    private String productReleaseQueue;
    /**
     * 交换机
     */
    private String productEventExchange;
    /**
     * 第一个队列的路由key
     * 进入延迟队列的路由key
     */
    private String productReleaseDelayRoutingKey;
    /**
     * 第二个队列的路由key
     * 消息过期，进入释放死信队列的key
     */
    private String productReleaseRoutingKey;
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
    public Exchange productEventchange() {
        return new TopicExchange(productEventExchange, true, false);
    }

    /**
     * 延迟队列
     * 第一个队列:延迟队列，不能被监听消费
     *
     * @return
     */
    @Bean
    public Queue productReleaseDelayQueue() {

        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-routing-key", productReleaseRoutingKey);
        args.put("x-dead-letter-exchange", productEventExchange);
        args.put("x-message-ttl", ttl);

        return new Queue(productReleaseDelayQueue, true, false, false, args);
    }

    /**
     * 死信队列(使用普通队列)
     * 被消费者监听恢复库存的队列
     * 延迟队列的消息过期后转发的队列
     *
     * @return
     */
    @Bean
    public Queue productReleaseQueue() {
        return new Queue(productReleaseQueue, true, false, false);
    }

    /**
     * 第一个队列:延迟队列 与交换机 的绑定关系建立
     *
     * @return
     */
    @Bean
    public Binding productReleaseDelayQueueBinding() {
        return new Binding(productReleaseDelayQueue, Binding.DestinationType.QUEUE, productEventExchange, productReleaseDelayRoutingKey, null);
    }

    /**
     * 第二个队列:死信队列 与交换机 的绑定关系建立
     *
     * @return
     */
    @Bean
    public Binding productReleaseQueueBinding() {
        return new Binding(productReleaseQueue, Binding.DestinationType.QUEUE, productEventExchange, productReleaseRoutingKey, null);
    }

}
