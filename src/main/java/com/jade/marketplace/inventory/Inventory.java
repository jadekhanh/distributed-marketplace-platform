package com.jade.marketplace.inventory;

import java.time.LocalDateTime;

import com.jade.marketplace.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Inventory of a product
 * 
 * Note:
 * Product = listing/catalog information
 * Inventory = stock/count/availability control
 * 
 * Product data = name, description, price, category, images
 * Inventory data = stock changes during adding to cart, checking out, order cancelling, restocking, returning, refunding
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    /**
     * Generated ID by MySQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A column for product that this inventory belongs to
     * @OneToOne = one product only has one inventory record
     * FetchType.LAZY = only fetch product information when asked
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    /**
     * A column for total available quantity of the product
     */
    @Column(nullable = false)
    private Integer availableQuantity;

    /**
     * A column for total reserved quantity of the product
     */
    @Column(nullable = false)
    private Integer reservedQuantity;

    /**
     * A column for the time inventory was updated
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor
     */
    public Inventory(Product product, Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
        this.product = product;
        this.reservedQuantity = 0;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get inventory ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Get inventory product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Get available quantity
     */
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Get reserved quantity
     */
    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    /**
     * Get timestamp when inventory was last updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Checks whether enough stock is availble for reserved requested quantity of product
     */
    public boolean canReserve(Integer quantity) {
        return availableQuantity - reservedQuantity >= quantity;
    }

    /**
     * Reserved inventory for a pending order
     */
    public void makeReservation(Integer quantity) {
        // update reserved quantity only since we only update availble quantity when order is confirmed
        this.reservedQuantity += quantity;

        // update last time the inventory is updated
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Confirms reserved inventory after payment succeeds
     */
    public void confirmReservation(Integer quantity) {
        // update available quantity
        this.availableQuantity -= quantity;

        // update reserved quantity
        this.reservedQuantity -= quantity;

        // update last time the inventory is updated
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Release reservation when payment fails
     */
    public void releaseReservation(Integer quantity) {
        // update reserved quantity
        this.reservedQuantity -= quantity;
        if (this.reservedQuantity < 0) {
            this.reservedQuantity = 0;
        }

        // update last time the inventory is updated
        this.updatedAt = LocalDateTime.now();
    }

    
}
