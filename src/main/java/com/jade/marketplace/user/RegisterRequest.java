package com.jade.marketplace.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data required inside request to register a new user
 *
 * This is a record type instead of class because it only carries request data
 * Getter functions such as constructor, getEmail(), getPassword(), getfirstName(), getlastName() are automatically generates
 * Do not have setter functions
 */
public record RegisterRequest(

        // String email must be @NotBlank and @Email type
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        // String password must be @NotBlank and have a min @Size of 8
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        // String firstName must be @NotBlank
        @NotBlank(message = "First name is required")
        String firstName,

        // String lastName must be @NotBlank
        @NotBlank(message = "Last name is required")
        String lastName,

        // Role role must be @NotBlank
        @NotNull(message = "Role is required")
        Role role

) {
}