package com.jade.marketplace.cart;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

/**
 * GraphQL for mutating cart data
 */
@Controller
public class CartMutation {

    private final CartService cartService;

    /**
     * Constructor
     * @param cartService
     */
    public CartMutation(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Adds a product to the current user's cart
     */
    @MutationMapping
    public Cart addToCart(@Argument @Valid AddToCartRequest request) {
        return cartService.addToCart(request);
    }

    /**
     * Update a product from current user's cart
     */
    @MutationMapping
    public Cart updateCart(@Argument @Valid UpdateCartItemRequest request) {
        return cartService.updateCart(request);
    }

    /**
     * Remove a product from current user's cart
     */
    @MutationMapping
    public Cart removeFromCart(Long productId) {
        return cartService.removeFromCart(productId);
    }

    /**
     * Remove all products from current user's cart
     */
    @MutationMapping
    public Cart removeAllFromCart() {
        return cartService.removeAllFromCart();
    }

}
