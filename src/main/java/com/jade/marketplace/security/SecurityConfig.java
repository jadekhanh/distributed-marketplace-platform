package com.jade.marketplace.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main Spring Security configuration
 *
 * Defines:
 * - which endpoints are public
 * - which endpoints require authentication
 * - password hashing strategy
 * - JWT filter registration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // JWTFilter checks JWT token from incoming request
    private final JwtFilter jwtFilter;
    // UserDetailService loads user from database
    private final UserDetailsService userDetailsService;

    // constructor
    public SecurityConfig(JwtFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Get Spring's configured Authentication Manager
     * For example, to check password and email
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
        throws Exception {
            return config.getAuthenticationManager();
    }

    /**
     * Creates a password hash tool
     * Use:
     * - when register new user
     * - when validate password
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates Bean that knows how to authenticate
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // creates a provider that loads user info from UserDetails service
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        // compare raw and password hash
        provider.setPasswordEncoder(passwordEncoder());
        // give this provider to Spring
        return provider;
    }

    /**
     * Spring Security filter chain
     * Every request go throught a security filter chain: request -> filter 1 -> filter 2 -> ... -> controller
     * Defines how requests are secured 
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // start configuring HTTP security
        return http
                // disables CSFR protection since JWT APIs don't need browser cookies for auth
                .csrf(csrf -> csrf.disable())
                // enables CORS handling so frontend can call backend from another port
                .cors(cors -> {})
                // since JWT already carries auth proof so no need to create login session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // define which requests to authorize
                .authorizeHttpRequests(auth -> auth
                        // allow these endpoints without login / JWT
                        .requestMatchers("/health", "/graphql", "/graphiql").permitAll()
                        // other endpoints must login / JWT
                        .anyRequest().authenticated()
                )
                // register custom Authentication provider
                .authenticationProvider(authenticationProvider())
                // make sure JWT Filter runs before built-in UsernamePasswordAuthentication Fitler
                // JWT filter: validates token, authenticates user, sets Security Context
                // by the time it reaches UsernamePasswordAuthentication Fitler, token is already validated so skip this part
                // we don't want JWT filter happens to late
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // finish and build Spring Security chain
                .build();
    }

}