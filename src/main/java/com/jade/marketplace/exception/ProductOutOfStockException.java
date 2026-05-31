package com.jade.marketplace.exception;

/**
 * Throws when product out of stock and buyer tries to buy more than product availability
 */
public class ProductOutOfStockException extends RuntimeException {
    // constructor
    public ProductOutOfStockException(String message) {
        // calls constructor of parent class RuntimeException(String message)
        super(message);
    }
}