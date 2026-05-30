package com.jade.marketplace.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs once on every incoming HTTP request
 * Responsibilities:
 * 1. Look for Authorization header
 * 2. Extract JWT token
 * 3. Validate token
 * 4. Load user information
 * 5. Store authenticated user in SecurityContext
 *
 * After this filter runs, Spring Security knows
 * who the current user is
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // constructor
    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        // load JWT service
        this.jwtService = jwtService;
        // Spring Security interface representing authenticated user info
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filter request flow:
     * - extract token
     * - extract email from token
     * - load user from DB using email
     * - validate token
     * - create Authentication object
     * - put into SpringContextHolder
     * - Spring now see the request as authenticated
     * - continue to controller
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
    
    throws ServletException, IOException {

        /**
         * Get authorization header
         * Example:
         * Authorization: Bearer abc123
        */
        String authHeader = request.getHeader("Authorization");

        // if header is null or header doesn't start with "Bearer", stop
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // get token from Auth header: Bearer abc123
        // .substring(7) = extract everything after first 7 characters of Auth which is "Bearer "
        String token = authHeader.substring(7);
        // get email from token
        String email = jwtService.extractEmail(token);

        /**
         * SecurityContextHolder = Spring Security's current logged-in user's storage
         */
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // load user from database using email
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            // using JWT service, check if token is valid: 
            // signature correct, expiration not expired, and email inside token is the same as email from database
            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                // if token is valid, create an Authentication object which means that this user is authenticated
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                // logged-in user
                                userDetails,
                                // password set to null because user is already authenticated, no need
                                null,
                                // user roles: admin or user
                                userDetails.getAuthorities()
                        );
                // attach extra requests info such as IP address, session ID, etc.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // make Spring officially considers this user authenticated
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // pass this request to next controller, filter, endpoint
        // conceptually similar to nextFunction() in TypeScript
        filterChain.doFilter(request, response);
    }
}