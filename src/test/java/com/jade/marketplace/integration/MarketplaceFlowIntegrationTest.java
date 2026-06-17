package com.jade.marketplace.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;

/**
 * E2E marketplace flow test
 * 
 * Flow:
 * register seller
 * create seller profile
 * create category
 * create product
 * register buyer
 * add product to cart
 * place order
 * 
 * @SpringBootTest = start the entire app context -> load all controllers, services, repositories, JPA entities
 * @AutoConfigureGraphQLTester = creates GraphQLTester
 * @ActiceProfiles = loads application-test.yml -> prevent touching prod data
 */
@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class MarketplaceFlowIntegrationTest {

    /**
     * @Autowired = injects a ready to use graphQLTester
     */
    @Autowired
    private HttpGraphQlTester graphQlTester;

    /**
     * Marketplace flow should create product and place order
     */
    @Test
    void marketplaceFlow_shouldCreateProductAndPlaceOrder() {
        // GraphQL register mutation request
        // input request: email, password, first name, last name, role
        // output response: token, email, roles
        String registerSellerMutation = 
            """
                mutation {
                    register(request: {
                        email: "monkey@plushies.com",
                        password: "monkeyisfeelingfunny!",
                        firstName: "Monkey",
                        lastName: "Tran",
                        role = SELLER
                    }) {
                        token
                        email
                        roles
                        }
                    }
            """;
        
        // load and send the GraphQL register seller mutation request and return JSON response
        String sellerToken = graphQlTester.document(registerSellerMutation).execute().path("register.token").entity(String.class).get();

        // create GraphQL mutation to create a seller profile
        // input: store name, description
        // output: id, store name
        String createSellerProfileMutation = 
            """
                mutation {
                    createSellerProfile(request: {
                        storeName: "Plushies Store",
                        description: "Plushies store for Jade"
                    }) {
                        id
                        storeName}
                        }
            """;

        // create a seller profile
        graphQlTester
            .mutate()
            // adds an Authorization header
            .headers(headers -> headers.setBearerAuth(sellerToken))
            .build()
            // send the GraphQL request to create seller profile
            .document(createSellerProfileMutation)
            .execute()
            // go to JSON response, go to create seller profile ID and confirm it has a value
            .path("createSellerProfile.id")
            .hasValue();
        
        // create GraphQL mutation to create a category
        // input request: name, description
        // output response: id, name
        String categoryMutation = 
            """
                mutation {
                    createCategory(request: {
                        name: "Toys",
                        description: "Toys for kitty and plushies gang",
                    }) {
                        id
                        name
                        }
                    }
            """;
        
        // load and send the GraphQL register mutation request and get category ID
        String categoryId = graphQlTester.document(categoryMutation).execute().path("createCategory.id").entity(String.class).get();

        // create GraphQL mutation to create a product
        // input request: name, description, price, quantity, category id, image url
        // output response: id, name, quantity, price
        String productMutation = 
            """
                mutation {
                    createProduct(request: {
                        name: "Squishy ice cream",
                        description: "A pink squishy ice cream for plushies",
                        price: 12.99,
                        quantity: 10,
                        categoryId: "%s"
                        imageUrl: [https://plushies.com/icecream.jpg]
                    }) {
                        id
                        name
                        quantity
                        price
                        }
                    }
            """.formatted(categoryId); // replace placeholder with category id
        
        //  get product ID
        String productId = graphQlTester
            .mutate()
            // adds an Authorization header
            .headers(headers -> headers.setBearerAuth(sellerToken))
            // builds new GraphQL tester with the auth header attached
            .build()
            // load and send the GraphQL mutation request
            .document(productMutation)
            .execute()
            // get product ID
            .path("createProduct.id").entity(String.class).get();
        
        // GraphQL register buyer mutation request
        // input request: email, password, first name, last name, role
        // output response: token, email, roles
        String registerBuyerMutation = 
            """
                mutation {
                    register(request: {
                        email: "jellycat@plushies.com",
                        password: "jellycatwantstoslapeveryone!",
                        firstName: "Jelly Cat",
                        lastName: "Tran",
                        role = BUYER
                    }) {
                        token
                        email
                        roles
                        }
                    }
            """;
        
        // load and send the GraphQL register buyer mutation request and return JSON response
        String buyerToken = graphQlTester.document(registerBuyerMutation).execute().path("register.token").entity(String.class).get();

        // create a buyer using buyer token
        HttpGraphQlTester buyer = graphQlTester.mutate().headers(headers -> headers.setBearerAuth(buyerToken)).build();
        
        // create a GraphQL mutation to add item to buyer's cart
        String addToCartMutation = 
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
            """.formatted(productId); // replace placeholder with product ID

        // add items to buyer's cart and confirms that the JSON response has quantity entity that is equal to expected value = 1
        buyer.document(addToCartMutation).execute().path("addToCart.items[0].quantity").entity(Integer.class).isEqualTo(1);
    

        // create a GraphQL mutation to place an order
        String placeOrderMutation = 
            """
                mutation {
                    placeOrder {
                        id,
                        status,
                        totalAmount
                    }
                }        
            """;

        // place an order for the buyer
        buyer.document(placeOrderMutation).execute()
            // confirms JSON response has id entity that is not null
            .path("placeOrder.id").hasValue()
            // confirms JSON response has status entity that is PENDING
            .path("placeOrder.status").entity(String.class).isEqualTo("PENDING");

    }
}
