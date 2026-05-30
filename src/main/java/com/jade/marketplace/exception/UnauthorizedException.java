package com.jade.marketplace.exception;

/**
 * Thrown when a user is not authenticated
 *
 * Example:
 * - Missing JWT token
 * - Invalid JWT token
 */
public class UnauthorizedException extends RuntimeException {

    // constructor
    public UnauthorizedException(String message) {
        // calls the constructor of parent class RuntimeException(String message)
        super(message);
    }
}