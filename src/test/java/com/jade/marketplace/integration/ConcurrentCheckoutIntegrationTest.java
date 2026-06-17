package com.jade.marketplace.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.jade.marketplace.category.Category;
import com.jade.marketplace.category.CategoryRepository;
import com.jade.marketplace.inventory.Inventory;
import com.jade.marketplace.inventory.InventoryRepository;
import com.jade.marketplace.inventory.InventoryService;
import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductRepository;
import com.jade.marketplace.seller.SellerProfile;
import com.jade.marketplace.seller.SellerRepository;
import com.jade.marketplace.user.Role;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserRepository;

/**
 * Integration test for concurrency for inventory reservation
 * 
 * @SpringBootTest = start the entire app context -> load all controllers, services, repositories, JPA entities
 * @ActiceProfiles = loads application-test.yml -> prevent touching prod data
 */
@SpringBootTest()
@ActiveProfiles("test")
public class ConcurrentCheckoutIntegrationTest {
    
    /**
     * @Autowired = injects a ready to use graphQLTester
     */
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void cleanDatabase() {
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        sellerRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * A test where 20 buyers hitting inventory at the same time cannot oversell stock
     */
    @Test
    void concurrentReserveInventory_shouldNotOversell() throws InterruptedException {
        // create a user
        User user = new User("jade@plushies.com", "jadewillgetherofferletterthisyear!", "Jade", "Tran", Set.of(Role.SELLER));
        userRepository.save(user);

        // create a seller profile
        SellerProfile sellerProfile = new SellerProfile(user, "Plushies Gang Store", "Pink store that sells cutie plushies!");
        sellerRepository.save(sellerProfile);

        // create a category
        Category category = new Category("toys", "toys for everyone");
        categoryRepository.save(category);

        // create a product and save into repository
        Product product = new Product(sellerProfile, "pink ice cream", "ice cream for plushies gang", category, new BigDecimal(12.75), 10);
        productRepository.save(product);

        // create inventory for product
        Inventory inventory = new Inventory(product, product.getQuantity());
        inventoryRepository.save(inventory);

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
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // reduce latch after each time
                    latch.countDown();
                }
            });
        }

        // the test waits until latch = 0, when all workers are completed
        latch.await();

        // shut down thread pool
        executorService.shutdown();

        // read final inventory
        Inventory updatedInventory = inventoryService.findInventoryByProductId(product.getId());

        // assert that updated inventory is present
        assertNotNull(updatedInventory);

        // assert that reserved quantity = 10, available quantity = 10
        assertEquals(10, updatedInventory.getAvailableQuantity());
        assertEquals(10, updatedInventory.getReservedQuantity());
        
    }
}
