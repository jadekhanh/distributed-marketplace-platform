package com.jade.marketplace.payment;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

/**
 * GraphQL for mutating payment data
 */
@Controller
public class PaymentMutation {
    
    private final PaymentService paymentService;

    /**
     * Constructor
     */
    public PaymentMutation(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Create a payment for an order
     */
    @MutationMapping
    public Payment createPayment(@Valid @Argument CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    /**
     * Refund a payment for an order
     */
    @MutationMapping
    public Payment refundPayment(@Valid @Argument Long paymentId) {
        return paymentService.refundPayment(paymentId);
    }
}
