package com.jade.marketplace.user;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL queries related to users
 * Queries read data
 * 
 * Note: GraphQL has 2 operations: query and mutation
 * 1. Query only reads
 * 2. Mutation does everything else
 */
@Controller
public class UserQuery {

    // UserService handles all logic related to users
    private final UserService userService;

    public UserQuery(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the currently authenticated user using UserService's getCurrentUser()
     * Requires the request to include:
     * Authorization: Bearer <jwt-token>
     */
    @QueryMapping
    public User me() {
        return userService.getCurrentUser();
    }
}