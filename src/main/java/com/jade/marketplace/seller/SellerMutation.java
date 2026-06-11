package com.jade.marketplace.seller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

/**
 * GraphQL mutations for changing seller profile data
 */
@Controller
public class SellerMutation {

    private final SellerService sellerService;

    /**
     * Construction
     */
    public SellerMutation(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * Creates a seller profile for the current user
     */
    @MutationMapping
    public SellerProfile createSellerProfile(@Argument @Valid CreateSellerProfileRequest request) {
        return sellerService.createSellerProfile(request);
    }

    /**
     * Updates a seller profile for the current user
     */
    @MutationMapping
    public SellerProfile updateSellerProfile(@Argument @Valid UpdateSellerProfileRequest request) {
        return sellerService.updateSellerProfile(request);
    }

    /**
     * Deletes a seller profile for the current user
     */
    @MutationMapping
    public boolean deleteSellerProfile() {
        return sellerService.deleteSellerProfile();
    }
    
}
