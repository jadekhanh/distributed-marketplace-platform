package com.jade.marketplace.notification;

import com.jade.marketplace.common.constants.KafkaTopics;
import com.jade.marketplace.kafka.events.NotificationCreatedEvent;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for notification-related events
 */
@Component
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * Constructor
     * @param notificationService
     * @param userService
     */
    public NotificationConsumer(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    /**
     * Consumes order placed event
     */
    @KafkaListener(topics = KafkaTopics.ORDER_PLACED, groupId = "payment-service")
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        // get user from server side
        User user = userService.findById(event.userId());

        // create notification
        notificationService.createNotification(user ,event.type(),event.message());
    }
    
}
