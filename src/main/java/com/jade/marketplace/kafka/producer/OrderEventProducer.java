package com.jade.marketplace.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.jade.marketplace.common.constants.KafkaTopics;
import com.jade.marketplace.kafka.events.OrderCancelledEvent;
import com.jade.marketplace.kafka.events.OrderPlacedEvent;

/**
 * Kafka producer for order-related events
 * 
 * Note:
 * Kafka producer publishes events to Kafka topics and sends messages into Kafka
 */
@Component
public class OrderEventProducer {

    /**
     * A template to send message
     * String = message key
     * Object = event types
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Constructor
     */
    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes an order-placed event
     */
    public void publishOrderPlaced(OrderPlacedEvent event) {
        kafkaTemplate.send(KafkaTopics.ORDER_PLACED, event);
    }

    /**
     * Publishes an order-cancelled event
     */
    public void publishOrderCancelled(OrderCancelledEvent event) {
        kafkaTemplate.send(KafkaTopics.ORDER_CANCELLED, event);
    }
    
}
