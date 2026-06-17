package com.jade.marketplace.inventory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

/**
 * Database layer access for MySQL inventory records
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Finds inventory by product ID
     * Optional = means inventory might not exist
     */
    Optional<Inventory> findByProductId(Long id);

    /**
     * Finds inventory by product ID and lock it so nobody else can modify it until my transaction finishes 
     * This is to prevent overselling
     * @Query = this is to help MySQL find product ID for update
     * @Lock = when fetching this row, immediately lock it
     * LockModeType.PESSIMISTIC_WRITE = assumes conflicts will happen, lock it immediately
     * When transaction A calls findByProductIdForUpdate(), database locks row immediately, so when transaction B tries to access the same row, it is blocked until transaction A commits the transaction
     * Optional = means inventory might not exist
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.product.id = :productId")
    Optional<Inventory> findByProductIdForUpdate(Long productId);
}
