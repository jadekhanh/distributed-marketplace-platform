package com.jade.marketplace.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.jade.marketplace.common.constants.KafkaTopics;
import com.jade.marketplace.inventory.InventoryService;
import com.jade.marketplace.kafka.events.OrderPlacedEvent;
import com.jade.marketplace.kafka.producer.OrderEventProducer;

/**
 * Integration test for Kafka event publishing
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Run this test independently")
public class KafkaIntegrationTest {
    
    @Autowired
    private OrderEventProducer orderEventProducer;

    // Kafka ConsumerFactory that creates Kafka consumers
    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    @MockitoBean
    private InventoryService inventoryService;

    /**
     * Veryify OrderEventProducer publishes OrderPlacedEvent and the arrives in the Kafka topic
     */
    @Test
    void publishOrderPlace_shouldSendEventToKafkaTopic() {

        /**
         * Creates Kafka consumer
         * groupId = Kafka tracks consumers using group ids
         * clientId: for logging and monitoring
         */
        var consumer = consumerFactory.createConsumer("kafka-integration-test" + UUID.randomUUID(), "kafka-integration-test-client");

        // tell consumer to listen to ORDER_PLACED topic
        consumer.subscribe(java.util.List.of(KafkaTopics.ORDER_PLACED));

        // tell Kafka to assign partitions
        consumer.poll(Duration.ofSeconds(1));

        // skip old messages already in ORDER_PLACED topic
        consumer.seekToEnd(consumer.assignment());

        // create order placed event with orderId, productId, quantity
        OrderPlacedEvent event = new OrderPlacedEvent(1L, 10L, 2);

        // publish event and send message to Kafka
        orderEventProducer.publishOrderPlaced(event);

        // read message from Kafka
        ConsumerRecords<String, Object> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

        ConsumerRecord<String, Object> message = null;

        for (ConsumerRecord<String, Object> record : records.records(KafkaTopics.ORDER_PLACED)) {
            OrderPlacedEvent value = (OrderPlacedEvent) record.value();

            if (value.orderId().equals(1L)
                    && value.productId().equals(10L)
                    && value.quantity() == 2) {
                message = record;
                break;
            }
        }

        // assert message exists
        assertNotNull(message);

        // assert message topic is ORDER_PLACED
        assertEquals(KafkaTopics.ORDER_PLACED, message.topic());

        // extract message body
        OrderPlacedEvent body = (OrderPlacedEvent) message.value();

        // assert order ID
        assertEquals(1L, body.orderId());

        // assert product ID
        assertEquals(10L, body.productId());

        // assert quantity
        assertEquals(2, body.quantity());

        // close consumer and disconnect from Kafka
        consumer.close();
    }
}
