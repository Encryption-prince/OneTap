package com.coding.OneTap.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;

@Component
public class RedisConnectionTest implements CommandLineRunner {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisConnectionTest(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            stringRedisTemplate.opsForValue().set("testKey", "HelloRedis");
            String value = stringRedisTemplate.opsForValue().get("testKey");
            System.out.println("✅ Redis connected, test value: " + value);
        } catch (Exception e) {
            System.err.println("❌ Redis connection failed: " + e.getMessage());
        }
    }
}
