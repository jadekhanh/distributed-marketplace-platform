package com.jade.marketplace.cart;

import java.math.BigDecimal;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL queries for reading cart data
 */
@Controller
public class CartQuery {
    
    private final CartService cartService;

    /**
     * Constructor
     */
    public CartQuery(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Returns the current user's cart
     */
    @QueryMapping
    public Cart getCart() {
        return cartService.getCart();
    }

    /**
     * Return the total costs of the user's cart
     */
    @QueryMapping
    public BigDecimal getCartTotal() {
        return cartService.calculateCartTotal();
    }
}
