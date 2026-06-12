package com.jade.marketplace.payment;

import com.jade.marketplace.common.constants.KafkaTopics;
import com.jade.marketplace.kafka.events.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for payment-related workflow 
 * 
 * Purpose: listens for order events and automatically reserves inventory when an order is placed
 * 
 * Flow of marketplace: 
 * Buyer places order
 * Order Service publishes ORDER_PLACED which Kafka stores
 * Then, 
 * Inventory Service consumes ORDER_PLACED to reserves stock
 * Notification Service consumes ORDER_PLACED to sends email
 * Payment Service consumes ORDER_PLACED to process payment
 * In general, 1 event -> many consumers
 * 
 * With Kafka, publish ORDER_PLACED -> other services decide what to do
 */
@Component
public class PaymentConsumer {

    private final PaymentService paymentService;

    /**
     * Constructor
     * @param paymentService
     */
    public PaymentConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    /**
     * Consumes order placed event
     */
    @KafkaListener(topics = KafkaTopics.ORDER_PLACED, groupId = "payment-service")
    public void handleOrderPlaced(OrderPlacedEvent event) {

    }

}
