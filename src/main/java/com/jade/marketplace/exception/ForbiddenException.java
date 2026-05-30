package com.jade.marketplace.exception;

/**
 * Throw when an unauthenticated user does not have permission
 * 
 * Example:
 * - Buyer tries to edit seller product
 * - Seller tries to edit inventory
 */
public class ForbiddenException extends RuntimeException {

    // constructor
    public ForbiddenException(String messgae) {
        // calls constructor of parent class RuntimeException(String message)
        super(message);
    }
}