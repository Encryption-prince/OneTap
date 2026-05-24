package com.coding.OneTap.controller;

import com.coding.OneTap.service.RedisUsageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/redis")
public class RedisUsageController {

    private final RedisUsageService redisUsageService;

    public RedisUsageController(RedisUsageService redisUsageService) {
        this.redisUsageService = redisUsageService;
    }

    @GetMapping("/usage")
    public Map<String, Object> getRedisUsage() {
        return redisUsageService.getRedisStats();
    }
}
