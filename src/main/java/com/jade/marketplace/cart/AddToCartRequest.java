package com.jade.marketplace.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Data required to request to add item to cart
 */
public record AddToCartRequest (

    // Note: inside request, only have IDs and not the object itself because client should not send all information of the product for security reasons (e.g., they cannot send product price)
    @NotBlank(message = "Product id is required")
    Long productId,

    @NotBlank(message = "Product quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity

) {
    
}
