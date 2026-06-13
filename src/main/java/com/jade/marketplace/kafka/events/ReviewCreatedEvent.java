package com.jade.marketplace.kafka.events;

/**
 * Event published when a review is added
 */
public record ReviewCreatedEvent (

    Long reviewId,

    Long productId,

    Long userId,

    Integer rating
    
) {
    
}
