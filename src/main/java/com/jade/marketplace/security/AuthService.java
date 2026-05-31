package com.jade.marketplace.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.jade.marketplace.user.LoginResponse;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserService;

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
    private final UserService userService;

    // constructor
    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = null;
    }

    /**
     * Authenticates a user and returns a JWT response
     * Flow:
     * 1. Validate email/password
     * 2. Spring Security loads user from database
     * 3. Password is verified
     * 4. JWT token is generated
     * 5. Token is returned to client
     */
    public LoginResponse login(String email, String password) {

        // authenticate user with email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password)
        );

        // get user from authentication
        User user = userService.findByEmail(authentication.getName());

        // get JWT token from user email and authorities
        String token = jwtService.generateToken(user.getEmail(), user.getAuthorities());

        // generates JWT login response from user and token
        return userService.toLoginResponse(user, token);
    }
}