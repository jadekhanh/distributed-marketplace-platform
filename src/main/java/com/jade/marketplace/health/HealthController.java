package com.jade.marketplace.health;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Application health check controller to verify that the application is still running
 */
@RestController
public class HealthController {
    
    /**
     * Returns application status
     * GET / health
     */
    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("plushies up!", "jade-and-plushies-gang-distributed-marketplace-platform", LocalDateTime.now());
    }

}
