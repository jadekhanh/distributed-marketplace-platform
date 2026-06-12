package com.jade.marketplace.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for access data for payment
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Finds payment by order
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * Check if a payment already exists for an order
     */
    boolean existsByOrderId(Long orderId);
    
}
