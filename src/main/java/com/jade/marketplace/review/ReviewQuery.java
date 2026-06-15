package com.jade.marketplace.review;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

/**
 * GraphQL for query review data
 */
@Controller
public class ReviewQuery {

    private final ReviewService reviewService;

    /**
     * Constructor
     */
    public ReviewQuery(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Get a review by its ID
     */
    @QueryMapping
    public Review review(@Valid @Argument Long id) {
        return reviewService.findById(id);
    }

    /**
     * Get all reviews belonging to a product in decsending order of created at timestamp
     */
    @QueryMapping
    public List<Review> reviewsByProduct(@Valid @Argument Long productId) {
        return reviewService.findByProductIdOrderByCreatedAtDesc(productId);
    }
    
}
