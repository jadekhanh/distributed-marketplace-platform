package com.jade.marketplace.kafka.events;

import java.math.BigDecimal;

/**
 * Event published when payment is processed
 */
public record PaymentProcessedEvent (

    Long paymentId,

    Long orderId,

    BigDecimal totalAmount
    
) {
    
}
