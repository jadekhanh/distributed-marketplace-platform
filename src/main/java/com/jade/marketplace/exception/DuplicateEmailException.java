package com.jade.marketplace.exception;

/**
 * Throws when user tries to register with an email that already exists
 */
public class DuplicateEmailException extends RuntimeException {
    // constructor
    public DuplicateEmailException(String message) {
        // calls the constructor of parent class RuntimeException(String message)
        super(message);
    }
}
