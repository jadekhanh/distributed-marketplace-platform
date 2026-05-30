package com.jade.marketplace.user;

import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL mutations related to users
 *
 * Mutations change data
 * Register change data so it is a mutation to GraphQL endpoint
 *
 * Example:
 *
 * mutation {
 *   register(input: {
 *     email: "jade@gmail.com",
 *     password: "password123",
 *     firstName: "Jade",
 *     lastName: "Tran",
 *     role: BUYER
 *   }) {
 *     token
 *     email
 *   }
 * }
 */
@Controller
public class UserMutation {

    /**
     * UserService handles user-related work: create user, hash password, save user, generate JWT
     */
    private final UserService userService;

    // construction
    public UserMutation(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user and returns a login response containing a JWT by calling UserService
     * @Argument = @RequestBody in REST
     * @Valid = indicates to validate this object before entering method
     * 
     * Flow:
     * public LoginResponse register(
     *      // take GraphQL input
     *      @Argument
     * 
     *      // convert into Java object
     *      RegisterRequest input
     * 
     *      // validate it
     *      @Valid
     * 
     *      // userService.register()
     * 
     *      // save user into database
     *      
     *      // generate JWT token
     * 
     *      // return login response
     * )
     */
    @MutationMapping
    public LoginResponse register(@Argument @Valid RegisterRequest input) {
        return userService.register(input);
    }
}