package com.jade.marketplace.payment;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import jakarta.validation.Valid;

/**
 * GraphQL for query payment data
 */
public class PaymentQuery {

    private final PaymentService paymentService;

    /**
     * Constructor
     */
    public PaymentQuery(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Get a payment by its ID
     */
    @QueryMapping
    public Payment payment(@Valid @Argument Long paymentId) {
        return paymentService.findById(paymentId);
    }
    

    /**
     * Get a payment by its order ID
     */
    @QueryMapping
    public Payment paymentByOrder(@Valid @Argument Long orderId) {
        return paymentService.findByOrderId(orderId);
    }
}
