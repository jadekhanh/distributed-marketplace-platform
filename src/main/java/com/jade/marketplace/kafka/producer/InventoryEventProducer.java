package com.jade.marketplace.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.jade.marketplace.common.constants.KafkaTopics;
import com.jade.marketplace.kafka.events.InventoryReservationFailedEvent;
import com.jade.marketplace.kafka.events.InventoryReservedEvent;

/**
 * Kafka producer for inventory-related events
 * 
 * Note:
 * Kafka producer publishes events to Kafka topics and sends messages into Kafka
 */
@Component
public class InventoryEventProducer {
    
    /**
     * A template to send message
     * String = message key
     * Object = event types
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Constructor
     */
    public InventoryEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes inventory-reserved event
     */
    public void publishInventoryReserved(InventoryReservedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event);
    }

    /**
     * Publishes inventory-reservation-failed event
     */
    public void publishInventoryReservationFailed(InventoryReservationFailedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVATION_FAILED, event);
    }

}
