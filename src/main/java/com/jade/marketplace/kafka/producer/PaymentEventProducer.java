package com.jade.marketplace.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.jade.marketplace.common.constants.KafkaTopics;
import com.jade.marketplace.kafka.events.PaymentFailedEvent;
import com.jade.marketplace.kafka.events.PaymentProcessedEvent;

/**
 * Kafka producer for payment-related events
 * 
 * Note:
 * Kafka producer publishes events to Kafka topics and sends messages into Kafka
 */
@Component
public class PaymentEventProducer {
    
    /**
     * A template to send message
     * String = message key
     * Object = event types
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Constructor
     */
    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a payment-processed event
     */
    public void publishPaymentProcessed(PaymentProcessedEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_PROCESSED, event);
    }

    /**
     * Publishes a payment-failed event
     */
    public void publishPaymentProcessed(PaymentFailedEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, event);
    }

}
