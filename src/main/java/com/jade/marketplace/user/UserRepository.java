package com.jade.marketplace.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA = Java Persistence API 
 * A specification that tells Java apps to:
 * Save objects to a database
 * Read objects from a database
 * Update objects in a database
 * Delete objects from a database
 * 
 * Here, implement a custom JPA respository for User entities
 * Some function from JPARepository are:
 * save(user)
 * findById(id)
 * findAll()
 * deleteById(id)
 * existsById(id)
 * count()
 * 
 * MySQL database access layer for User entities
 * 
 * Flow architecture:
 * - Controller
 * - Service
 * - UserRepository
 * - JPA/Hibernate
 * - Database
 * 
 * Note:
 * - User class = Java representation of "users" table
 * - User repository = tool to search, delete, edit "users" table in MySQL
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Go to MySQL database and finds a user by their email
     * Used during login and JWT authentication
     * Flow:
     * - Go to database
     * - find User through UserRepository findbyEmail()
     * - get User user
     * - get User's email through User getEmail()
     * 
     * Optional to indicate there might or might be not User by this email
     * Use Optional to avoid NullPointerException error and just go straight to ResourceNotFoundException
     * 
     * Even though there's no implementation to this function, JPA knows that we're looking for email by reading method name
     */
    Optional<User> findByEmail(String email);

    /**
     * Go to MySQL database and checks whether an email is already registered
     * Used during registration to prevent duplicate accounts
     * 
     * Even though there's no implementation to this function, JPA knows that we're looking for email by reading method name
     */
    boolean existsByEmail(String email);
}