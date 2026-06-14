package com.jade.marketplace.redis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.micrometer.common.lang.NonNull;

/**
 * Handles Redis caching for cart-related data
 * 
 * Customers might want to retrieve cart multiple times before checking out
 * 
 * Note:
 * EV has 2 common uses:
 * 1. Caching: this project
 * 2: Distributed locking / coordination: my EV project
 * 
 * With Redis, for a cart that is recently viewed, subsequent requests touch MySQL 
 * 
 * Flow:
 * request data
 * if cache exists in Redis -> return
 * if not -> go to MySQL -> then save into Redis -> return
 */
@Service
public class CartCacheService {
    
    // cart key for specific user "cart:user:" + userId (e.g., cart:user:1)
    private static final String CART_KEY_PREFIX = "cart:user:";
    // TTL = time to live = Redis deletes the cache entry after 30 minutes
    private static final Duration CART_CACHE_TTL = Duration.ofMinutes(30);
    // Spring helper class talking to Redis 
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Constructor
     */
    public CartCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Stores cart JSON in Redis only after 30 minutes
     */
    public void cacheCart(@NonNull Long cartId, @NonNull String cartJson) {
        // build cart key
        String key = CART_KEY_PREFIX + cartId;

        // store cached cart JSON (cart value) into Redis only for 30 minutes
        redisTemplate.opsForValue().set(key, cartJson, CART_CACHE_TTL);
    }

    /**
     * Gets cached cart JSON from Redis
     */
    public String getCachedCart(Long cartId) {
        // build cart key
        String key = CART_KEY_PREFIX + cartId;

        // get cached cart JSON from Redis
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Remove cart from Redis cache when cart is updated or deleted
     */
    public void removeCart(Long cartId) {
        // build cart key
        String key = CART_KEY_PREFIX + cartId;

        // remove cached cart JSON from Redis
        redisTemplate.delete(key);
    }

}
