package com.jade.marketplace.seller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data required to create a seller profile
 */
public record CreateSellerProfileRequest (

    @NotBlank(message = "Store name is required")
    @Size(max = 100, message = "Store name can only have at most 100 characters")
    String storeName,

    @NotBlank(message = "Store description is required")
    @Size(max = 1000, message = "Store description can only have at most 1000 characters")
    String description
    
) {

}
