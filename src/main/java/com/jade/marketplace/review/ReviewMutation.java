package com.jade.marketplace.review;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

/**
 * GraphQL for mutating review data
 */
@Controller
public class ReviewMutation {
    
    private final ReviewService reviewService;

    /**
     * Constructor
     */
    public ReviewMutation(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Create a review for a product
     */
    @MutationMapping
    public Review createReview(@Valid @Argument CreateReviewRequest request) {
        return reviewService.createReview(request);
    }

    /**
     * Update a review for a product
     */
    @MutationMapping
    public Review updateReview(@Valid @Argument UpdateReviewRequest request) {
        return reviewService.updateReview(request);
    }

    /**
     * Delete a review for a product
     */
    @MutationMapping
    public boolean deleteReview(@Valid @Argument Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

}
