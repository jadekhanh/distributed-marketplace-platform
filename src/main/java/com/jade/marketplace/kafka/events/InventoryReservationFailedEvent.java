package com.jade.marketplace.kafka.events;

/**
 * Event published when an inventory failed to reserve for an order item
 */
public record InventoryReservationFailedEvent (

    Long orderId,

    Long productId,

    Integer quantity,

    String reason
    
) {
    
}
