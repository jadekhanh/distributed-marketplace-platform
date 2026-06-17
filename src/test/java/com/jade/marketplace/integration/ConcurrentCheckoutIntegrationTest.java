package com.jade.marketplace.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.jade.marketplace.inventory.Inventory;
import com.jade.marketplace.inventory.InventoryRepository;
import com.jade.marketplace.inventory.InventoryService;
import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductRepository;

/**
 * Integration test for concurrency for inventory reservation
 * 
 * @SpringBootTest = start the entire app context -> load all controllers, services, repositories, JPA entities
 * @ActiceProfiles = loads application-test.yml -> prevent touching prod data
 */
@SpringBootTest
@ActiveProfiles("test")
public class ConcurrentCheckoutIntegrationTest {
    
    /**
     * @Autowired = injects a ready to use graphQLTester
     */
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * A test where 20 buyers hitting inventory at the same time cannot oversell stock
     */
    @Test
    void concurrentReserveInventory_shouldNotOversell() throws InterruptedException {
        // get the first product inside repository
        Product product = productRepository.findAll().stream().findFirst().orElseThrow();

        // get inventory of product
        // if inventory does not exist, create a new inventory for it with quantity = 10
        Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElseGet(() -> inventoryRepository.save(new Inventory(product, 10)));

        // simulate 20 buyers where each of them tries to reserve 1 item
        int threads = 20;

        // create an Executor Service to create and manage 20 worker threads
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        // Synchronization object that is used to wait for all threads to finish
        CountDownLatch latch = new CountDownLatch(threads);

        // launch threads
        for (int i = 0; i < threads; i++) {
            // for each thread, try to reserve 1 item simulatenously
            executorService.submit(() -> {
                try {
                    inventoryService.reserveInventory(product.getId(), 1);
                } catch (Exception ignored) {
                    // after 10 requests, the rest is going to fail since stock runs out
                } finally {
                    // reduce latch after each time
                    latch.countDown();
                }
            });
        }

        // the test waits until latch = 0, when all workers are completed
        latch.wait();

        // shut down thread pool
        executorService.shutdown();

        // read final inventory
        Inventory updatedInventory = inventoryService.findInventoryByProductId(product.getId());

        // assert that updated inventory is present
        assertNotNull(updatedInventory);

        // assert that reserved quantity = available quantity = 10
        assertEquals(updatedInventory.getAvailableQuantity(), 10);
        assertEquals(updatedInventory.getReservedQuantity(), 10);
        
    }
}
