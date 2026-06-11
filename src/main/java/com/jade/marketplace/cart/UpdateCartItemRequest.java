package com.jade.marketplace.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Data required to request update cart item
 */
public record UpdateCartItemRequest (

    @NotBlank(message = "Product id is required")
    Long productId,

    @NotBlank(message = "Product quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
    
) {}
