package com.jade.marketplace.user;

import com.jade.marketplace.exception.DuplicateEmailException;
import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * User service handles all logic for users
 *
 * Responsibilities:
 * - Register users
 * - Find users
 * - Get the currently authenticated user
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // constructor
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new marketplace user
     *
     * Default behavior:
     * - If no role is provided, user becomes BUYER.
     * - Password is hashed with BCrypt before saving.
     * - JWT token is returned immediately after registration.
     */
    public LoginResponse register(RegisterRequest request) {
        // use UserRepository to confirm if this email is already in the MySQL database
        // if returns true, this email is already in the database, which is already registered
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email is already registered: " + request.email());
        }

        // select role(s) if not already specified in request body
        Role selectedRole;
        if (request.role() == null) {
            // if not specified, set role as BUYER
            selectedRole = Role.BUYER;
        } else {
            // if specified, just parse it
            selectedRole = request.role();
        }

        // create new User from request information
        User user = new User(
                request.email(),
                // encode password using password encoder algorithm
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                Set.of(selectedRole)
        );

        // save this User into MySQL using UserRepository
        User savedUser = userRepository.save(user);

        // generate JWT token using JWTService with email and authorities
        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getAuthorities()
        );

        // return a LoginResponse
        return toLoginResponse(savedUser, token);
    }

    /**
     * Finds a user by id by using UserRepository's findById()
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id)
                );
    }

    /**
     * Finds a user by email by using UserRepository's findByEmail()
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email)
                );
    }

    /**
     * Returns the currently authenticated user
     */
    public User getCurrentUser() {
        /**
         * Authentication object used by Spring to store information about a logged-in user
         * Example:
         * Name: jade@gmail.com
         * Role: BUYER
         * Authenticated: true
         * 
         * SecurityContext is where Spring stores a Authentication object
         * SecurityContextHolder is global storage area for all SecurityContext
         */
        Authentication authentication =
                // .getContext() = get SecurityContext
                // .getAuthentication() = get the currently logged-in user
                SecurityContextHolder.getContext().getAuthentication();

        // get user's email from Authentication object
        // in this project: email is saved in name
        String email = authentication.getName();

        // return User using UserRepository's findByEmail()
        return findByEmail(email);
    }

    /**
     * Converts a User entity into a LoginResponse record
     */
    public LoginResponse toLoginResponse(User user, String token) {
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles()
        );
    }
}