package com.jade.marketplace.notification;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jade.marketplace.exception.ForbiddenException;
import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserService;

import jakarta.transaction.Transactional;

/**
 * Notification service handles all logic related to notification
 */
@Service
public class NotificationService {

    private final UserService userService;
    private final NotificationRepository notificationRepository;

    /**
     * Constructor
     */
    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    /**
     * Creates a notification for a user
     */
    public Notification createNotification(User user, NotificationType type, String message) {
        // create a notification
        Notification notification = new Notification(user, type, message);

        // return saved notification
        return notificationRepository.save(notification);
    }

    /**
     * Returns all notifications for the current user in descending order
     */
    public List<Notification> findAllNotifications() {
        // get user from server
        User user = userService.getCurrentUser();

        // return all notifications belonging to the user
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Returns all unread notification for the current user in descending order
     */
    public List<Notification> findAllUnreadNotifications() {
        // get user from server
        User user = userService.getCurrentUser();

        // return all unread notifications belonging to the user
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Marks a notification as read
     */
    @Transactional
    public Notification markAsRead(Long notificationId) {
        // get notification
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("Cannot find notification by its id: " + notificationId));

        // get user from server side
        User user = userService.getCurrentUser();

        // check if user matches the user which the notification belongs to
        if (!user.equals(notification.getUser())) {
            throw new ForbiddenException("User does not have permission to modify this notification");
        }

        // mark notification as read
        notification.markAsRead();

        // return saved notification
        return notificationRepository.save(notification);
    }


    /**
     * Marks all notifcations as read
     */
    @Transactional
    public boolean markAllAsRead() {
        // get user from server side
        User user = userService.getCurrentUser();

        // get all unread notifications belonged to the user
        for (Notification notification : notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user)) {
            // mark them as read
            notification.markAsRead();

            // save them into repository
            notificationRepository.save(notification);
        }

        return true;

    }
    
}
