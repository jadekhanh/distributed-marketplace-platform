package com.jade.marketplace.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data required to process a payment
 * 
 * Payment method token is a token created by payment processor such as Stripe or Paypal
 * When we provide credit card information, payment processor creates a token of the credit card which is then later used and stored in backend so that our credit card information is never stored
 */
public record CreatePaymentRequest (

    @NotNull(message = "Order id is required")
    Long orderId,

    @NotBlank(message = "Payment method token is required")
    String paymentMethodToken

) {
    
}
