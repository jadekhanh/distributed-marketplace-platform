package com.jade.marketplace.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads user details for Spring Security
 *
 * Spring Security calls this during:
 * - login
 * - JWT validation
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // constructor
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by email using UserService
     * Spring Security calls this method using the login username
     * In our project, username = email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // use UserRepository's findByEmail() to return User
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );
    }
}