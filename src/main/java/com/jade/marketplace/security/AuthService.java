package com.jade.marketplace.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Handles user authentication logic
 * Responsibilities:
 * - Verify user credentials
 * - Generate JWT tokens after successful login
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates a user and returns a JWT token
     * Flow:
     * 1. Validate email/password
     * 2. Spring Security loads user from database
     * 3. Password is verified
     * 4. JWT token is generated
     * 5. Token is returned to client
     */
    public String login(String email, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        return jwtService.generateToken(
                authentication.getName(),
                authentication.getAuthorities()
        );
    }
}