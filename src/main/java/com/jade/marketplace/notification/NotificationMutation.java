package com.jade.marketplace.notification;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

/**
 * GraphQL for mutating notification data
 */
@Controller
public class NotificationMutation {
    
    private final NotificationService notificationService;

    /**
     * Constructor
     */
    public NotificationMutation(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Marks one notification as read
     */
    @MutationMapping
    public Notification markAsRead(@Valid @Argument Long notificationId) {
        return notificationService.markAsRead(notificationId);
    }

    /**
     * Marks all unread notifications as read
     */
    @MutationMapping
    public boolean markAllAsRead() {
        return notificationService.markAllAsRead();
    }
}
