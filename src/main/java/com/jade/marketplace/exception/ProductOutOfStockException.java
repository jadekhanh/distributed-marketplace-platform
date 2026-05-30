package com.jade.marketplace.exception;

/**
 * Throws when product out of stock and buyer tries to buy more than product availability
 */
public class ProductOfOutStock extends RuntimeException {
    // constructor
    public ProductOfOutStock(String message) {
        // calls constructor of parent class RuntimeException(String message)
        super(message);
    }
}