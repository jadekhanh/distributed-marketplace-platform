package com.jade.marketplace.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A custom JPA repository for Product entities
 * MySQL database access layer for Product entities
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products in a category
     */
    List<Product> findByCategoryId(Long id);

    /**
     * Find all products from a seller profile
     */
    List<Product> findBySellerProfileId(Long id);

    /**
     * Searches product names containing a keyword
     */
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
}
