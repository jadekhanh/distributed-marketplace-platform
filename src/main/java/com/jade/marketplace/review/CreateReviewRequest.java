package com.jade.marketplace.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data required to request to create a review
 * Only comment is optional
 */
public record CreateReviewRequest (

    @NotNull(message = "Product id is required")
    Long productId,

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    Integer rating,

    String comment
    
){
    
}
