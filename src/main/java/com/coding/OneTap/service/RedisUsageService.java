package com.coding.OneTap.service;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class RedisUsageService {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisUsageService(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public Map<String, Object> getRedisStats() {
        Map<String, Object> stats = new HashMap<>();

        try (RedisConnection connection = redisConnectionFactory.getConnection()) {

            // Fetch memory and keyspace info
            Properties memoryInfo = connection.info("memory");
            Properties keyspaceInfo = connection.info("keyspace");

            long usedMemory = parseLong(memoryInfo.getProperty("used_memory"));
            long peakMemory = parseLong(memoryInfo.getProperty("used_memory_peak"));
            long totalKeys = extractTotalKeys(keyspaceInfo);

            stats.put("usedMemoryMB", String.format("%.2f", usedMemory / (1024.0 * 1024.0)));
            stats.put("peakMemoryMB", String.format("%.2f", peakMemory / (1024.0 * 1024.0)));
            stats.put("totalKeys", totalKeys);
            stats.put("status", "✅ Redis connected successfully");

        } catch (Exception e) {
            stats.put("status", "❌ Redis connection failed: " + e.getMessage());
        }

        return stats;
    }

    private long parseLong(String value) {
        try {
            return value != null ? Long.parseLong(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long extractTotalKeys(Properties keyspaceInfo) {
        long totalKeys = 0;
        try {
            for (String key : keyspaceInfo.stringPropertyNames()) {
                String info = keyspaceInfo.getProperty(key);
                // Example: "keys=23,expires=0,avg_ttl=0"
                if (info != null && info.contains("keys=")) {
                    String[] parts = info.split(",");
                    for (String part : parts) {
                        if (part.startsWith("keys=")) {
                            totalKeys += Long.parseLong(part.split("=")[1]);
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return totalKeys;
    }
}


