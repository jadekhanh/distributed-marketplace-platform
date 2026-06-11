package com.jade.marketplace.inventory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jade.marketplace.common.constants.KafkaTopics;

/**
 * A Kafka consumer for inventory-related events
 * 
 * Purpose: listens for order events and automatically reserves inventory when an order is placed
 * 
 * Flow of marketplace: 
 * Buyer places order
 * Order Service publishes ORDER_PLACED which Kafka stores
 * Then, 
 * Inventory Service consumes ORDER_PLACED to reserves stock
 * Notification Service consumes ORDER_PLACED to sends email
 * Analytics Service consumes ORDER_PLACED to update metrics
 * In general, 1 event -> many consumers
 * 
 * With Kafka, publish ORDER_PLACED -> other services decide what to do
 */
@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;

    /**
     * Constructor
     */
    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Consumes order placed events and reserves product inventory
     * @KafkaListener = listens to the ORDER_PLACED Kafka topic. Whenever a message arrives, ORDER_PLACED calls reserveInventory()
     * Topic = a channel. Examples: ORDER_PLACED, ORDER_CANCELLED, PAYMENT_COMPLETE, USER_REGISTERED
     * groupId = consumer group name
     */
    @KafkaListener(topics = KafkaTopics.ORDER_PLACED, groupId = "inventory-service")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        // reserves inventory by requested product and quantity
        inventoryService.reserveInventory(event.productId(), event.quantity());
    }

}
