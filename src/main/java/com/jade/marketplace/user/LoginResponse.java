package com.jade.marketplace.user;

import java.util.Set;

/**
 * Response returned after successful login or registration
 * Includes the JWT token so the client can use it for authenticated requests
 * 
 * Note: this is a Java record so it is a short version of Java class which automatically
 * creates a constructor with all the specified params and all getters for all params
 */
public record LoginResponse(
        String token,
        Long userId,
        String email,
        String firstName,
        String lastName,
        Set<Role> roles
) {
}