package com.jade.marketplace.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data required inside request to login a user
 *
 * This is a record type instead of class because it only carries request data
 * Getter functions such as constructor, getEmail(), getPassword(), getfirstName(), getlastName() are automatically generates
 * Do not have setter functions
 */
public record LoginRequest(
    
    // String email must be @NotBlank and @Email type
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    // String password must be @NotBlank and have a min @Size of 8
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password

) {

}
