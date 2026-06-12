package com.jade.marketplace.notification;

import java.util.List;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL for query notification data
 */
@Controller
public class NotificationQuery {
    
    private final NotificationService notificationService;

    /**
     * Constructor
     * @param notificationService
     */
    public NotificationQuery(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Returns all notifications belonging to a user
     */
    @QueryMapping
    public List<Notification> findAllNotifications(String userId) {
        return notificationService.findAllNotifications();
    }

    /**
     * Returns all unread notifications belonging to a user
     */
    @QueryMapping
    public List<Notification> findAllUnreadNotifications(String userId) {
        return notificationService.findAllUnreadNotifications();
    }
}
