package com.jade.marketplace.seller;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jade.marketplace.user.User;

/**
 * Database access layer for seller profiles
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
 */
@Repository
public interface SellerRepository extends JpaRepository<SellerProfile, Long>{ 

    /**
     * Go to MySQL database and finds a seller profile by the owning user
     * Even though there's no implementation to this function, JPA knows that we're looking for Seller Profile by reading method name
     */
    Optional<SellerProfile> findByUser(User user);

    /**
     * Go to MySQL database and checks whether a user already has a seller profile
     */
    boolean existsByUser(User user);

    /**
     * Go to MySQL and checks whether a store name already exists
     */
    boolean existsByStoreName(String storeName);

    
}
