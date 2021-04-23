package com.mo.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * 配置分布式锁的RedissonClient
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
