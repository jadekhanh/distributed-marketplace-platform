package com.jade.marketplace.kafka.events;

/**
 * Event published when order is created
 * 
 * Consumers:
 * Inventory Service reserves stock
 * Payment Service can process payment
 * Notification Service notify buyer/seller
 */
public record OrderPlacedEvent (

    Long orderId,

    Long productId,

    Integer quantity
    
) {
    
}
