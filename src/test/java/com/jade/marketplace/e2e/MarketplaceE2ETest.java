package com.jade.marketplace.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.jade.marketplace.inventory.InventoryRepository;
import com.jade.marketplace.inventory.Inventory;
import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductRepository;
import com.jade.marketplace.redis.CartCacheService;
import com.jade.marketplace.redis.ProductCacheService;

/**
 * E2E test for marketplace flow
 * 
 * Flow:
 * seller registration
 * seller profile creation
 * category creation
 * product creation
 * inventory creation
 * buyer registration
 * cart update
 * order placement
 * payment processing
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MarketplaceE2ETest {
    
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @MockitoBean
    private ProductCacheService productCacheService;

    @MockitoBean
    private CartCacheService cartCacheService;

    @BeforeEach
    void cleanDatabase() {
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    /**
     * Send GraphQL request
     * This is for request that do not need user authentication token, anyone can make this type of request
     * @return raw JSON repsonse from GraphQL
     */
    private String sendGraphQLRequest(String query) {
        // create a request body with a map, in which key = "query", value = query
        Map<String, String> body = Map.of("query", query);

        // use Spring's TestRestTemplate to send HTTP POST request and retrieve response as string
        // "http://localhost:" + port + "/graphql" = GraphQL endpoint url with local host
        return testRestTemplate.postForObject("http://localhost:" + port + "/graphql", body, String.class);
    }

    /**
     * Registers a user and extract token from GraphQL response
     * @return user token
     */
    private String registerAndExtractToken(String email, String firstName, String lastName, String role) {
        // create GraphQL request to register
        String registerMutation = 
            """
                mutation {
                    register(request: {
                        email: "%s",
                        password: "whoopiewhoopiedoop",
                        firstName: "%s",
                        lastName: "%s",
                        role: %s
                    }) {
                        email
                        token
                        roles
                        }
                }
            """.formatted(email, firstName, lastName, role);

        // send GraphQL request to register and receive raw JSON register response from GraphQL
        String registerResponse = sendGraphQLRequest(registerMutation);
        
        // create a pattern to look for token inside the register response
        Pattern pattern = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");

        // create a matcher that searches inside the register response
        Matcher matcher = pattern.matcher(registerResponse);

        // if matcher cannot find token pattern inside register response
        if (!matcher.find()) {
            throw new IllegalStateException("Token not found in response: " + registerResponse);
        }

        // return token
        return matcher.group(1);
    }

    /**
     * Send GraphQL request with user token
     * This is for requests that require authentication token to prevent unauthorized permission
     */
    private String sendAuthenticationGraphQL(String token, String query) {
        // create HTTP headers
        HttpHeaders headers = new HttpHeaders();

        // adds content type to be application JSON to tell server that request body contains JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // set authorization token bearer
        headers.setBearerAuth(token);

        // create GraphQL request body
        Map<String, String> body = Map.of("query", query);

        // create a HTTP request object with combined request body + authorization headers
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // send POST request to target GraphQL endpoint url and HTTP request object and returns the response as a string
        return testRestTemplate.postForObject("http://localhost:" + port + "/graphql", request, String.class);

    }

    /**
     * Extract ID from JSON response
     */
    private String extractIdFromResponse(String response) {
        // create a pattern to look for id inside the response
        Pattern pattern = Pattern.compile("\"id\"\\s*:\\s*\"?(\\d+)\"?");

        // create a matcher that searches inside the response
        Matcher matcher = pattern.matcher(response);

        // if matcher cannot find id pattern inside response
        if (!matcher.find()) {
            throw new IllegalStateException("ID not found in response: " + response);
        }

        // return id
        return matcher.group(1);
    }

    @Test
    void fullMarketFlow_shouldWorkAsExpected() {
        // create seller token
        String sellerToken = registerAndExtractToken("penguincat@plushies.com", "Penguin Cat", "Tran", "SELLER");

        // GraphQL mutation query to create seller
        String sellerMutation = 
            """
                mutation {
                    createSellerProfile(request: {
                        storeName: "Pluhies Gang Store",
                        description: "Sell plushies gangsters"
                    }) {
                        id
                        storeName
                        }
                }
            """;

        // create seller with seller auth token
        String sellerResponse = sendAuthenticationGraphQL(sellerToken, sellerMutation);

        // asserts that seller JSON response contains store name
        assertTrue(sellerResponse.contains("Pluhies Gang Store"));

        // GraphQL mutation query to create category
        String categoryMutation = 
            """
                mutation {
                    createCategory(request: {
                        name: "Toys",
                        description: "Toys for kitty and plushies gang"
                    }) {
                        id
                        name
                        }
                    }
            """;

        // create category response without auth token
        String categoryResponse = sendGraphQLRequest(categoryMutation);

        // extract ID from response
        String categoryId = extractIdFromResponse(categoryResponse);

        // create GraphQL mutation to create a product
        String productMutation = 
            """
                mutation {
                    createProduct(request: {
                        name: "Ghostie ghostie",
                        description: "A Halloween ghostie to take care of you when you sleep!",
                        price: 35.95,
                        quantity: 10,
                        categoryId: "%s",
                        url: "https://plushies.com/ghostie.jpg"
                    }) {
                        id
                        name
                        quantity
                        price
                        }
                    }
            """.formatted(categoryId); // replace placeholder with category id
        
        // create product with seller auth token
        String productResponse = sendAuthenticationGraphQL(sellerToken, productMutation);

        // asserts that product JSON response contains product name
        assertTrue(productResponse.contains("Ghostie ghostie"));

        // extract product ID from product JSON response
        String productId = extractIdFromResponse(productResponse);

        // create inventory for product
        Product product = productRepository.findById(Long.valueOf(productId)).orElseThrow();
        inventoryRepository.save(new Inventory(product, 10));

        // create a buyer token
        String buyerToken = registerAndExtractToken("mochi@plushies.com", "Mochi", "Tran", "BUYER");

        // create GraphQL mutation to create buyer's cart
        String cartMutation = 
                """
                    mutation {
                        addToCart(request: {
                            productId: "%s",
                            quantity: 1
                        }) {
                            id
                            items {
                                quantity
                                }
                            }
                    }                
                """.formatted(productId);
        
        // create cart response with buyer auth token
        String cartResponse = sendAuthenticationGraphQL(buyerToken, cartMutation);

        // assert cart JSON response contains quantity entity
        assertTrue(cartResponse.contains("quantity"));

        // create GraphQL mutation to create an order
        String orderMutation = 
                """
                    mutation {
                        placeOrder {
                            id
                            status
                            totalAmount
                        }
                    }
                """;
        
        // create order response with buyer token
        String orderResponse = sendAuthenticationGraphQL(buyerToken, orderMutation);

        // asserts order JSON response says CONFIRMED status
        assertTrue(orderResponse.contains("CONFIRMED"));

        // extract order ID from order JSON response
        String orderId = extractIdFromResponse(orderResponse);

        // GraphQL mutation to create payment
        String paymentMutation = 
            """
                mutation {
                    createPayment(request: {
                        orderId: "%s",
                        paymentMethodToken: "this-token-is-super-yummy!"
                    }) {
                        id
                        status
                        totalAmount
                        }
                }    
            """.formatted(orderId);
        
        // create payment response with buyer token
        String paymentResponse = sendAuthenticationGraphQL(buyerToken, paymentMutation);

        // asserts payment JSON response says "COMPLETED"
        assertTrue(paymentResponse.contains("COMPLETED"));

    }
}
