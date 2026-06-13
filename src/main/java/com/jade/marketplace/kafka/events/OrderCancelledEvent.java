package com.jade.marketplace.kafka.events;

/**
 * Event published when an order is cancelled
 */
public record OrderCancelledEvent (

    Long orderId,

    Long userId,

    String reason
    
) {
    
}
