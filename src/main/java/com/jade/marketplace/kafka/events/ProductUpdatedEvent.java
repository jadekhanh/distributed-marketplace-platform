package com.jade.marketplace.kafka.events;

/**
 * Event published when product is updated
 */
public record ProductUpdatedEvent (

    Long productId,

    Long userId
    
) {
    
}
