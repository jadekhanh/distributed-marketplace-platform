package com.jade.marketplace.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jade.marketplace.user.User;

/**
 * Cart repository for database access for carts in MySQL
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds the cart owned by a user
     */
    Optional<Cart> findByUser(User user);
    
}
