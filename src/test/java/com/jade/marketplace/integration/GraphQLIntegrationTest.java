package com.jade.marketplace.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

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

    /**
     * Query categories should return a list of categories
     */
    @Test
    void categories_shouldReturnListofCategories() {
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
