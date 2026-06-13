package com.jade.marketplace.kafka.events;

/**
 * Event published when a review is added
 */
public record ReviewAddedEvent (

    Long reviewId,

    Long productId,

    Long userId,

    Integer rating
    
) {
    
}
