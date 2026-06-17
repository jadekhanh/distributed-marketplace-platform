package com.jade.marketplace.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * E2E authentication test
 * 
 * Flow:
 * user registers and returns token
 * user logs in and returns token
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Both register and login should return authentication token
     */
    @Test
    void registerAndLogin_shouldReturnJwtTokens() {
        // GraphQL mutation to register
        // input request: email, password, first name, last name, role
        // output response: token, email, role
        String registerMutation = """
                mutation {
                    register(request: {
                        email: "echle@plushies.com",
                        password: "echlekhongcopybaiban!",
                        firstName: "Ech Le",
                        lastName: "Tran",
                        role: BUYER
                    }) {
                        token
                        email
                        roles
                        }
                    }
                """;
        
        // creates request body
        Map<String, String> registerBody = Map.of("query", registerMutation);

        // send registration request and return response as a String
        String registerResponse = restTemplate.postForObject("http://localhost:" + port + "graphql", registerBody, String.class);

        // asserts response contains token
        assertTrue(registerResponse.contains("token"));

        // asserts response contains expected email
        assertTrue(registerResponse.contains("echle@plushies.com"));

        // GraphQL mutation to login
        // input request: email, password
        // output response: email, token
        String loginMutation = """
                mutation {
                    register(request: {
                        email: "echle@plushies.com",
                        password: "echlekhongcopybaiban!"
                    }) {
                        email
                        token
                        }
                    }
                """;
        
        // creates request body
        Map<String, String> loginBody = Map.of("query", loginMutation);

        // send login request and return response as a String
        String loginResponse = restTemplate.postForObject("http://localhost:" + port + "graphql", loginBody, String.class);

        // asserts response contains token
        assertTrue(loginResponse.contains("token"));

        // asserts response contains expected email
        assertTrue(loginResponse.contains("echle@plushies.com"));
        
    }
    
}
