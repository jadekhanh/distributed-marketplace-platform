package com.jade.marketplace.kafka.events;

/**
 * Event published when inventory is reserved for an order item
 */
public record InventoryReservedEvent (

    Long productId,

    Integer quantity
    
) {
    
}
