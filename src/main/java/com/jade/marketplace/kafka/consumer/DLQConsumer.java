package com.jade.marketplace.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jade.marketplace.common.constants.KafkaTopics;

/**
 * Consumer for dead-letter queue (DLQ) events
 * 
 * DLQ happens when an event repeatedly fails to process, Kafka will send here so the system does not keep trying forever and we can process the reason for the failure
 */
@Component
public class DLQConsumer {

    /**
     * Handles failed Kafka events
     * Currently, we just log the failed event
     */
    @KafkaListener(topics = KafkaTopics.DEAD_LETTER, groupId = "dlq-service")
    public void handleDeadLetterEvent(Object event) {
        System.err.println("Failed Kafka event in dead-letter queue (DLQ): " + event);
    }
    
}
