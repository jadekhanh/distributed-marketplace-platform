package com.jade.marketplace.inventory;

import com.jade.marketplace.exception.ProductOutOfStockException;
import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.product.Product;

import jakarta.transaction.Transactional;

/**
 * Inventory Services handles all logic related to inventory
 */
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;

    /**
     * Constructor
     */
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Creates an inventory record for a product
     */
    public Inventory createInventory(Product product, Integer quantity) {
        // create new inventory
        Inventory inventory = new Inventory(product, quantity);

        // return saved inventory
        return inventoryRepository.save(inventory);
    }

    /**
     * Finds inventory by product ID
     */
    public Inventory findInventoryByProductId(Long id) {
        // return inventory or throw error
        return inventoryRepository.findByProductId(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + id));
    }

    /**
     * Reserves product inventory by product ID and quantity for a pending order
     */
    @Transactional
    public Inventory reserveInventory(Long id, Integer quantity) {
        // find product inventory or throw new error
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + id));

        // check if we can can reserve by requested quantity
        if (!inventory.canReserve(quantity)) {
            throw new ProductOutOfStockException("Product out of stock!");
        }

        // reserve product by quantity
        inventory.confirmReservation(quantity);

        // return saved inventory
        return inventoryRepository.save(inventory);
    }

    /**
     * Confirms reserved inventory after payment succeeds
     */
    @Transactional
    public Inventory confirmReservation(Long id, Integer quantity) {
        // find product inventory or throw new error
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + id));

        // reserve product by quantity
        inventory.confirmReservation(quantity);

        // return saved inventory
        return inventoryRepository.save(inventory);
    }

    /**
     * Release reserved inventory when payment fails
     */
    @Transactional
    public Inventory releaseReservation(Long id, Integer quantity) {
        // find product inventory or throw new error
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + id));

        // release product by quantity
        inventory.releaseReservation(quantity);

        // return saved inventory
        return inventoryRepository.save(inventory);
    }

}
