package com.jade.marketplace.seller;

import jakarta.validation.constraints.Size;

/**
 * Data required for updating seller profile
 * All fields are optional so seller can only update what is requested
 */
public record UpdateSellerProfileRequest (

    @Size(max = 100, message = "Store name can only have at most 100 characters")
    String storeName,

    @Size(max = 1000, message = "Store description can only have at most 1000 characters")
    String description

) {

}
