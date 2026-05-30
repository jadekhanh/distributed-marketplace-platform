package com.jade.marketplace.exception;

/**
 * Throws an error when input data is invalid
 * 
 * Example:
 * - Invalid product quantity
 * - Invalid order request
 * - Invalid product price
 */
public class ValidationException extends RuntimeException {

    // constructor
    public ValidationException(String message) {
        // calls the constructor of parent class RuntimeException(String message)
        super(message);
    }
}