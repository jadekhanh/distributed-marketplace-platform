package com.jade.marketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for backend
 * It configures CORS so a frontend running locally can call GraphQL endpoint
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Allows local frontend apps to send GraphQL requests to this backend
     * localhost:3000 is commonly used by React
     * localhost:5173 is commonly used by Vite
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/graphql")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}