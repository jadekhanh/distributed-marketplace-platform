package com.jade.marketplace.seller;

import java.util.List;

/**
 * GraphQL queries for reading seller data
 */
public class SellerQuery {

    private final SellerService sellerService;

    /**
     * Constructor
     */
    public SellerQuery (SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * Return all seller profiles
     */
    List<SellerProfile> sellers() {
        return sellerService.findAllSellers();
    }

    /**
     * Return one seller by their id
     */
    SellerProfile seller(Long id) {
        return sellerService.findById(id);
    }

    /**
     * Return the seller owned by current user
     */
    SellerProfile sellerProfile() {
        return sellerService.getSellerProfile();
    }
    
}
