package com.jade.marketplace.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jade.marketplace.user.User;

/**
 * A custom repository to access data layer for order in MySQL
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders placed by a buyer in descending order of when they are created
     */
    List<Order> findByBuyerOrderByCreatedAtDesc(User buyer);
} 
