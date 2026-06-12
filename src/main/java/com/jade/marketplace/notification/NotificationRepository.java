package com.jade.marketplace.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jade.marketplace.user.User;

/**
 * Repository for access data for notification
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Finds notifications for a user, newest first
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Finds unread notifications for a user, newest first 
     */
    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);
    
}
