package com.jade.marketplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration defines how the application communicates with Redis
 * Redis is used as a fast in-memory cache for data like product pages, carts, and frequently accessed marketplace information
 */
@Configuration
public class RedisConfig {

    /**
     * Redis template acts as main helper object used to read/write data in Redis
     * Template stores keys and values for easy inspection during development
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();

        // tell RedisTemplate which Redis connection to use
        template.setConnectionFactory(connectionFactory);

        // converts Java strings into Redis-readable text
        StringRedisSerializer serializer = new StringRedisSerializer();

        // configure Redis keys and values to be stored as strings
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);

        // configure Redis hash keys and hash values to be stored as strings
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);

        // finalize template setup before Spring uses it
        template.afterPropertiesSet();
        
        return template;
    }
}