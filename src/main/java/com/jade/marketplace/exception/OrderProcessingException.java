package com.jade.marketplace.exception;

/**
 * Throws an error when order process cannot be completed
 * 
 * Example:
 * - Payment failed
 * - Kafka event processing failed
 * - Inventory reservation failed
 */
public class OrderProcessingException extends RuntimeException {
    // constructor
    public OrderProcessingException(String message) {
        // calls constructor of parent class RuntimeException(String message)
        super(message);
    }
}