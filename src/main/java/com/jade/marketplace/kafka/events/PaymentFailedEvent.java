package com.jade.marketplace.kafka.events;

import java.math.BigDecimal;

/**
 * Event published when payment is failed
 */
public record PaymentFailedEvent (
    Long paymentId,

    Long orderId,

    BigDecimal totalAmount,

    String reason
) {
    
}
