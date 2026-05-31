package com.jade.marketplace.security;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.jade.marketplace.user.LoginResponse;

/**
 * In GraphQL, there are 2 main operation types:
 * 1. Query: similar to GET in REST API
 * query {
 *  product {
 *      name
 *      product
 *  }
 * }
 * 2. Mutation: similar to PUT in REST API
 * mutation {
 *  login(
 *      email: "jade@gmail.com",
 *      password: "password123"
 *  )
 * }
 * 
 * GraphQL mutations related to authentication
 * Login is considered mutation operation because it authenticates users, generates JWT, and changes authentication state
 * 
 * This class is a controller
 * Spring scans it and registers handlers
 */
@Controller
public class AuthMutation {

    // Auth Service handles login work: verify email and passwork, generate JWT token
    private final AuthService authService;

    // constructor
    public AuthMutation(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Logs in a user and returns a JWT token
     * Flow:
     * - calls AuthMutation.login()
     * - calls AuthService.login()
     * - calls AuthenticationManager.authenticate()
     * - JWT created
     * - token returns to frontend
     * - frontend stores token
     * - future requests sends token to every request
     */
    @MutationMapping
    public LoginResponse login(@Argument String email, @Argument String password) {
        return authService.login(email, password);
    }
}