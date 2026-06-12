package com.jade.marketplace.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jade.marketplace.product.Product;
import com.jade.marketplace.user.User;

/**
 * Repository for data access layer for review
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds reviews by product ID in descending order of created at timestamp
     */
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    /**
     * Checks whether a user already reviewed a product
     */
    boolean existsByProductAndUser(Product product, User user);

    /**
     * Finds reviews belonging to user in descending order of created at timestamp
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(User userId);
    
}
