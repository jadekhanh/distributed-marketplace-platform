package com.jade.marketplace.kafka.events;

/**
 * Event published when notification is created
 */
public record NotificationCreatedEvent (

    Long userId,

    Long notificationId,

    String message
    
) {
    
}
