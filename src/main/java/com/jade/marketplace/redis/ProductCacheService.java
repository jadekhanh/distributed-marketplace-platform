package com.jade.marketplace.redis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Handles Redis-caching for product-related data to avoid repeatedly querying MySQL for frequently viewed products
 * 
 * Note:
 * EV has 2 common uses:
 * 1. Caching: this project
 * 2: Distributed locking / coordination: my EV project
 * 
 * With Redis, for a product that is recently viewed, subsequent requests touch MySQL 
 * 
 * Flow:
 * request data
 * if cache exists in Redis -> return
 * if not -> go to MySQL -> then save into Redis -> return
 */
@Service
public class ProductCacheService {

    // product key "product:" + productId (e.g., product:1)
    private static final String PRODUCT_KEY_PREFIX = "product:";
    // TTL = time to live = Redis deletes the cache entry after 30 minutes
    private static final Duration PRODUCT_CACHE_TTL = Duration.ofMinutes(30);
    // Spring helper class talking to Redis
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Constructor
     */
    public ProductCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Stores product JSON in Redis only for 30 minutes
     */
    public void cacheProduct(@NonNull Long productId, @NonNull String productJson) {
        // build product key
        String key = PRODUCT_KEY_PREFIX + productId;

        // store cached product JSON (product value) into Redis only for 30 minutes
        redisTemplate.opsForValue().set(key, productJson, PRODUCT_CACHE_TTL);
    }

    /**
     * Gets cached product JSON from Redis
     */
    public String getCachedProduct(Long productId) {
        // build product key
        String key = PRODUCT_KEY_PREFIX + productId;

        // get cached product JSON from Redis
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Remove product from Redis cache when product is updated or deleted
     */
    public void removeProduct(Long productId) {
        // build product key
        String key = PRODUCT_KEY_PREFIX + productId;

        // remove cached product JSON from Redis
        redisTemplate.delete(key);
    }
    
}
