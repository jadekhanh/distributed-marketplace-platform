package com.jade.marketplace.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * A custom JPA repository for Category entities
 * MySQL database access layer for Category entities
 * 
 * Note: 
 * Category class = Java representation of "category" table
 * Category repository =  tool to search, delete, edit "category" table in MySQL
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Go to MySQL database and find a category by its name
     * Optional to indicate there might or might not a category by this name
     * Use Optional to avoid NullPointerException error and just go straight to ResourceNotFoundException
     */
    Optional<Category> findByName(String name);

    /**
     * Go to MySQL database and check if a category exists by its name
     */
    boolean existsByName(String name);
}