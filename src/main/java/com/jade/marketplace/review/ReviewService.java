package com.jade.marketplace.review;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jade.marketplace.exception.ForbiddenException;
import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.exception.ValidationException;
import com.jade.marketplace.kafka.events.ReviewCreatedEvent;
import com.jade.marketplace.kafka.producer.ReviewEventProducer;
import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductService;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserService;

import jakarta.transaction.Transactional;

/**
 * Review service handles all logic related to review
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ReviewEventProducer reviewEventProducer;

    /**
     * Constructor
     */
    public ReviewService(ReviewRepository reviewRepository, UserService userService, ProductService productService, ReviewEventProducer reviewEventProducer) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.productService = productService;
        this.reviewEventProducer = reviewEventProducer;
    }

    /**
     * Finds a review by its id
     */
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cannot find review with id: " + id));
    }

    /**
     * Find all reviews belonging to a product
     */
    public List<Review> findByProductIdOrderByCreatedAtDesc(Long productId) {
        // return all reviews in descending order of created at timestamp
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    /**
     * Create a review for a product
     */
    @Transactional
    public Review createReview(CreateReviewRequest request) {
        // get user from server
        User user = userService.getCurrentUser();

        // get product
        Product product = productService.findById(request.productId());

        // check if the user already reviews the product
        if (reviewRepository.existsByProductAndUser(product, user)) {
            throw new ValidationException("User already leaves review for this product with id: " + product.getId());
        }

        // create a review
        Review review = new Review(user, product, request.rating(), request.comment());

        // save review
        Review savedReview = reviewRepository.save(review);

        // publish review-created Kafka event
        reviewEventProducer.publishReviewCreated(new ReviewCreatedEvent(savedReview.getId(), product.getId(), user.getId(), request.rating()));

        // return saved review
        return savedReview;
    }

    /**
     * Update a review for a product
     */
    @Transactional
    public Review updateReview(UpdateReviewRequest request) {
        // get review
        Review review = reviewRepository.findById(request.reviewId()).orElseThrow(() -> new ResourceNotFoundException("Cannot find review with id: " + request.reviewId()));

        // get current user from server
        User user = userService.getCurrentUser();

        // check if the current owner is the same as the review's owner
        if (!user.equals(review.getUser())) {
            throw new ForbiddenException("User does not have permission to modify review with id: "+ request.reviewId());
        }

        // update review
        review.update(request.comment(), request.rating());

        // return saved review
        return reviewRepository.save(review);
    }

    /**
     * Delete a review for a product
     */
    @Transactional
    public boolean deleteReview(Long reviewId) {
        // get review
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Cannot find review with id: " + reviewId));

        // get current user from server
        User user = userService.getCurrentUser();

        // check if the current owner is the same as the review's owner
        if (!user.equals(review.getUser())) {
            throw new ForbiddenException("User does not have permission to modify review with id: "+ reviewId);
        }

        // delete review from database
        reviewRepository.delete(review);

        // return true to mark completed
        return true;
    }
    
}
