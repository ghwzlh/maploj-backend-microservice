package com.ghw.maplojbackendcommon.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 */
@Configuration
@Data
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        // 1. Create config object
        Config config = new Config();
        String redissonAddress = String.format("redis://127.0.0.1:6379");
        config.useSingleServer().setAddress(redissonAddress).setDatabase(1);

        // 2. Create Redisson instance
        return Redisson.create(config);
    }
}
