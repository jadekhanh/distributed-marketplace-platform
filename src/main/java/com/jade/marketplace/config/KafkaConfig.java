package com.jade.marketplace.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration
 * Kafka topics are event channels. Different services can publish to and consume from topics asynchronously
 */
@Configuration
public class KafkaConfig {

    /**
     * Topic published when a buyer places an order
     */
    @Bean
    public NewTopic orderPlacedTopic() {
        return TopicBuilder.name("order-placed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Topic published when inventory has been successfully reserved
     */
    @Bean
    public NewTopic inventoryReservedTopic() {
        return TopicBuilder.name("inventory-reserved")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Topic published when payment has been processed
     */
    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name("payment-processed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Topic published when a notification is created
     */
    @Bean
    public NewTopic notificationCreatedTopic() {
        return TopicBuilder.name("notification-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Dead-letter topic for failed events that cannot be processed successfully
     */
    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name("marketplace-dlq")
                .partitions(3)
                .replicas(1)
                .build();
    }
}