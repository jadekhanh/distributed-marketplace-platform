package com.jade.marketplace.exception;

/**
 * Thrown when a requested resource does not exist
 *
 * Example:
 * - User not found
 * - Product not found
 * - Order not found
 */
public class ResourceNotFoundException extends RuntimeException {

    // constructor
    public ResourceNotFoundException(String message) {
        // calls the constructor of parent class RuntimeException(String message)
        super(message);
    }
}