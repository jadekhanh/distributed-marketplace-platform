package com.jade.marketplace.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import com.jade.marketplace.category.Category;
import com.jade.marketplace.category.CategoryRepository;
import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductRepository;
import com.jade.marketplace.s3.ImageUploadService;
import com.jade.marketplace.seller.SellerProfile;
import com.jade.marketplace.seller.SellerRepository;
import com.jade.marketplace.user.Role;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserRepository;

import jakarta.transaction.Transactional;

/**
 * Integration test for image upload flow
 * 
 * Flow:
 * Create user, seller profile, category, and product
 * User upload an image for the product
 * Product image url is saved in MySQL database
 * Image bytes are sent to AWS S3
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires real AWS S3 credentials and bucket")
public class ImageUploadIntegrationTest {
    
    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Test upload image service can upload bytes to S3 and save url to MySQL
     */
    @Test
    @Transactional
    void uploadProductImage_shouldUploadBytesToS3AndSaveUrlToMySQL() {
        // create user
        User user = new User("yellokitty", "imissyousomuchbaby!", "Kitty", "Tran", Set.of(Role.SELLER));
        userRepository.save(user);

        // create seller profile
        SellerProfile sellerProfile = new SellerProfile(user, "Hello Kitty", "Hello Kitty Store in Diamond Plaza");
        sellerRepository.save(sellerProfile);

        // create category
        Category category = new Category("Clothes", "Clothes for Hello Kitty");
        categoryRepository.save(category);

        // create product
        Product product = new Product(sellerProfile, "Yellow hoodie", "Tiny hoodie for Hello Kitty", category, BigDecimal.valueOf(200.00), 1);
        productRepository.save(product);

        // get Spring Security's security context and sets the current authenticated user
        // pretend this user is logged in and passed authentication, give them their roles
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        // create a fake uploaded image file that behaves like a real uploaded image file
        MockMultipartFile file = new MockMultipartFile("file", "plushies-test.png", "image/png", "plushies-are-besties".getBytes());

        // upload product image for product
        Product updatedProduct = imageUploadService.uploadProductImage(product.getId(), file);

        // asserts that updated product has images
        assertFalse(updatedProduct.getImages().isEmpty());

        // asserts that there's only 1 image
        assertEquals(1, updatedProduct.getImages().size());

        // get the image url
        String url = updatedProduct.getImages().get(0).getUrl();

        // asserts url isn't blank
        assertFalse(url.isBlank());

    }
}
