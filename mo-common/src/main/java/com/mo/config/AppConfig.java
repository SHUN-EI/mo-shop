package com.mo.config;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by mo on 2021/4/24
 */
@Configuration
@Data
public class AppConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Value("${spring.redis.password}")
    private String redisPwd;

    /**
     * 避免存储的key乱码，hash结构不修改
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        RedisSerializer serializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);

        return redisTemplate;
    }

    /**
     * 配置分布式锁的RedissonClient
     *
     * @return
     */
    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();

        //单机模式
        config.useSingleServer().setPassword(redisPwd).setAddress("redis://" + redisHost + ":" + redisPort);

        //集群模式
//        config.useClusterServers().setScanInterval(2000)
//                .addNodeAddress("127.0.0.1:8000", "127.0.0.1:8001", "127.0.0.1:8002");


        RedissonClient client = Redisson.create(config);
        return client;

    }
}
