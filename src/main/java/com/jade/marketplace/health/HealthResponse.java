package com.jade.marketplace.health;

import java.time.LocalDateTime;

/**
 * Response returned by the health check endpoint
 */
public record HealthResponse (

    String status,

    String service,

    LocalDateTime time
    
) {
    
}
