package com.jade.marketplace.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.jade.marketplace.common.constants.KafkaTopics;
import com.jade.marketplace.kafka.events.ReviewCreatedEvent;

/**
 * Kafka producer for review-related events
 * 
 * Note:
 * Kafka producer publishes events to Kafka topics and sends messages into Kafka
 */
@Component
public class ReviewEventProducer {

    /**
     * A template to send message
     * String = message key
     * Object = event types
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Constructor
     */
    public ReviewEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a review-added event
     */
    public void publishReviewCreated(ReviewCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.REVIEW_ADDED, event);
    }

}
