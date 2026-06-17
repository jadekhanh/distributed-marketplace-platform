package com.jade.marketplace.integration;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import com.jade.marketplace.category.Category;
import com.jade.marketplace.category.CategoryRepository;
import com.jade.marketplace.inventory.InventoryRepository;
import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductRepository;
import com.jade.marketplace.seller.SellerProfile;
import com.jade.marketplace.seller.SellerRepository;
import com.jade.marketplace.user.Role;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserRepository;

/**
 * Integration tests for GraphQL smoke tests to verify schema loads and simple queries work properly
 * @SpringBootTest = start the entire app context -> load all controllers, services, repositories, JPA entities
 * @AutoConfigureGraphQLTester = creates GraphQLTester
 * @ActiceProfiles = loads application-test.yml -> prevent touching prod data
 */
@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class GraphQLIntegrationTest {
    /**
     * @Autowired = injects a ready to use graphQLTester
     */
    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void cleanDatabase() {
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        sellerRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Query categories should return a list of categories
     */
    @Test
    void categories_shouldReturnListofCategories() {
        // create a category
        Category category = new Category("plushies", "cute plushies");
        categoryRepository.save(category);
        
        // GraphQL query request
        // no input
        // output: a list of categories with id, name, description
        String query = """
                query {
                    categories {
                        id
                        name
                        description
                    }}
                """;
        
        // load and send the GraphQL mutation request and asserts categories section is not empty
        graphQlTester.document(query).execute().path("categories").hasValue();

    }

    /**
     * Query products should return a list of products
     */
    @Test
    void products_shouldReturnListofProducts() {
        // create a user
        User user = new User("jadetran@plushies.com", "jadewillgetherofferletterthisyear!", "Jade", "Tran", Set.of(Role.SELLER));
        userRepository.save(user);

        // create a seller profile
        SellerProfile sellerProfile = new SellerProfile(user, "Jade Plushies Store", "Pink store that sells cutie plushies!");
        sellerRepository.save(sellerProfile);

        // create a category
        Category category = new Category("toys", "toys for everyone");
        categoryRepository.save(category);

        // create a product and save into repository
        Product product = new Product(sellerProfile, "pink ice cream", "ice cream for plushies gang", category, new BigDecimal(12.75), 10);
        productRepository.save(product);
        
        // GraphQL query request
        // no input
        // output: a list of products with id, name, description, price, description, quantity
        String query = """
                query {
                    products {
                        id
                        name
                        price
                        description
                        quantity
                    }}
                """;
        
        // load and send the GraphQL mutation request and asserts categories section is not empty
        graphQlTester.document(query).execute().path("products").hasValue();

    }

}
