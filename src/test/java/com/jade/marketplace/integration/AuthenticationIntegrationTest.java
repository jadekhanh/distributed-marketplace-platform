package com.jade.marketplace.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for registration and login GraphQL flows
 * @SpringBootTest = start the entire app context -> load all controllers, services, repositories, JPA entities
 * @AutoConfigureGraphQLTester = creates GraphQLTester
 * @ActiceProfiles = loads application-test.yml -> prevent touching prod data
 */
@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class AuthenticationIntegrationTest {
    
    /**
     * @Autowired = injects a ready to use graphQLTester
     */
    @Autowired
    private GraphQlTester graphQlTester;

    /**
     * Register should create user and return token
     */
    @Test
    void register_shouldCreateUserAndReturnToken() {
        // GraphQL mutation request
        // input request: email, password, first name, last name, role
        // output response: token, email, roles
        String mutation = """
                mutation {
                    register(input: {
                        email: "macaroni@plushies.com",
                        password: "mauricemacaroniissad!",
                        firstName: "Maurice",
                        lastName: "Macaroni",
                        role = BUYER
                    }) {
                        token
                        email
                        roles
                        }
                    }
                """;
        
        // load and send the GraphQL mutation request
        graphQlTester.document(mutation).execute()
            // navigate into response JSON, go to token and assert token exists
            .path("register.token").hasValue()
            // navigate to response JSON, go to email, convert the value to String, and assert it equals to expected email
            .path("register.email").entity(String.class).isEqualTo("macaroni@plushies.com") 
            // navigate to response JSON, go to roles, convert the value to String, and assert it equals to expected role
            .path("register.roles").entity(String.class).isEqualTo("BUYER");
    }

    /**
     * Login should return token for existing user
     */
    @Test
    void login_shouldReturnTokenForExistingUser() {
        // GraphQL register mutation request
        // input request: email, password, first name, last name, role
        // output response: token, email, roles
        String registerMutation = """
                mutation {
                    register(input: {
                        email: "angy@plushies.com",
                        password: "angyisalwaysangy!",
                        firstName: "Angy",
                        lastName: "Tran",
                        role = BUYER
                    }) {
                        token
                        email
                        roles
                        }
                    }
                """;
        
        // load and send the GraphQL register mutation request
        graphQlTester.document(registerMutation).execute();
        
        // create GraphQl login mutation request
        // input: email, password
        // output: token, email
        String loginMutation = """
                mutation {
                    login(input: {
                        email: "angy@plushies.com",
                        password: "angyisalwaysangy!",
                    }) {
                        token
                        email
                        }
                }
                """;
        
        // load and send the GraphQL login mutation request
        graphQlTester.document(loginMutation).execute()
                // go to the JSON reponse, go to token, and confirms it has a value
                .path("login.token").hasValue()
                // go to the JSON reponse, go to email, convert it to String, and confirms it is the expected email
                .path("login.email").entity(String.class).isEqualTo("angy@plushies.com");
    }


}
