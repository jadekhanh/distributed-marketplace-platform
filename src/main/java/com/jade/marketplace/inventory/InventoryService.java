package com.jade.marketplace.inventory;

import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.springframework.stereotype.Service;

import com.jade.marketplace.exception.ProductOutOfStockException;
import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.kafka.events.InventoryReservationFailedEvent;
import com.jade.marketplace.kafka.events.InventoryReservedEvent;
import com.jade.marketplace.kafka.producer.InventoryEventProducer;
import com.jade.marketplace.product.Product;

import jakarta.transaction.Transactional;

/**
 * Inventory Services handles all logic related to inventory
 */
@Service
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer inventoryEventProducer;

    /**
     * Constructor
     */
    public InventoryService(InventoryRepository inventoryRepository, InventoryEventProducer inventoryEventProducer) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryEventProducer = inventoryEventProducer;
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
    public Inventory reserveInventory(Long productId, Integer quantity) {
        // find product inventory or throw new error
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId).orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));

        // check if the quantity requested is valid
        if (quantity == null || quantity <= 0) {
            throw new ValidateException("Quantity must be greater than 0!");
        }

        // check if we can can reserve by requested quantity
        if (!inventory.canReserve(quantity)) {
            // publish inventory reservation failed Kafka event
            inventoryEventProducer.publishInventoryReservationFailed(new InventoryReservationFailedEvent(productId, quantity, "Not enough inventory!"));

            // throw error
            throw new ProductOutOfStockException("Not enough inventory!");
        }

        // reserve product by quantity
        inventory.makeReservation(quantity);

        // save inventory into repository
        Inventory savedInventory = inventoryRepository.save(inventory);

        // publish inventory-reserved event
        inventoryEventProducer.publishInventoryReserved(new InventoryReservedEvent(productId, quantity));

        // return saved inventory
        return savedInventory;
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
