package com.sprintflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TEMPORARY Rate Limiting Configuration (Bucket4j disabled)
 * This is a simplified version to allow the application to start
 * 
 * TODO: Re-enable Bucket4j after fixing Maven dependencies
 * 
 * Rate Limits (currently not enforced):
 * - Login: 5 attempts per minute per IP
 * - General API: 100 requests per minute per user
 * - Public endpoints: 20 requests per minute per IP
 */
@Configuration
public class RateLimitConfig {

    // Simple counter-based rate limiting (temporary)
    private final Map<String, RateLimitInfo> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, RateLimitInfo> apiRequests = new ConcurrentHashMap<>();
    private final Map<String, RateLimitInfo> publicRequests = new ConcurrentHashMap<>();

    private static class RateLimitInfo {
        int count;
        long windowStart;
        
        RateLimitInfo() {
            this.count = 1;
            this.windowStart = System.currentTimeMillis();
        }
    }

    /**
     * Check login rate limit: 5 attempts per minute
     */
    public boolean checkLoginLimit(String key) {
        return checkLimit(loginAttempts, key, 5, 60000);
    }

    /**
     * Check API rate limit: 500 requests per minute
     */
    public boolean checkApiLimit(String key) {
        return checkLimit(apiRequests, key, 500, 60000);
    }

    /**
     * Check public endpoint rate limit: 20 requests per minute
     */
    public boolean checkPublicLimit(String key) {
        return checkLimit(publicRequests, key, 20, 60000);
    }

    private boolean checkLimit(Map<String, RateLimitInfo> map, String key, int maxRequests, long windowMs) {
        long now = System.currentTimeMillis();
        
        RateLimitInfo info = map.compute(key, (k, v) -> {
            if (v == null) {
                return new RateLimitInfo();
            }
            
            // Reset if window expired
            if (now - v.windowStart > windowMs) {
                v.count = 1;
                v.windowStart = now;
            } else {
                v.count++;
            }
            return v;
        });
        
        return info.count <= maxRequests;
    }

    /**
     * Cleanup old entries every 5 minutes
     */
    @Scheduled(fixedRate = 300000)
    public void cleanup() {
        long now = System.currentTimeMillis();
        long threshold = 600000; // 10 minutes
        
        loginAttempts.entrySet().removeIf(e -> now - e.getValue().windowStart > threshold);
        apiRequests.entrySet().removeIf(e -> now - e.getValue().windowStart > threshold);
        publicRequests.entrySet().removeIf(e -> now - e.getValue().windowStart > threshold);
    }
}
