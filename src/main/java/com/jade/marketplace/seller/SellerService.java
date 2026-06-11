package com.jade.marketplace.seller;

import com.jade.marketplace.user.User;

import java.util.List;

import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.exception.ValidationException;
import com.jade.marketplace.user.Role;
import com.jade.marketplace.user.UserRepository;
import com.jade.marketplace.user.UserService;

import jakarta.transaction.Transactional;

/**
 * Seller Profile service handles all logic for seller profiles
 * 
 * Responsibilities:
 * Create a seller profile
 * Update seller profile
 * Find seller profiles
 */
public class SellerService {
    
    private final SellerRepository sellerRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Constructor
     */
    public SellerService(SellerRepository sellerRepository, UserService userService, UserRepository userRepository) {
        this.sellerRepository = sellerRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Creates a seller profile for the currently authenticated user
     * Also adds SELLER role to the user
     */
    @Transactional
    public SellerProfile createSellerProfile(CreateSellerProfileRequest request) {

        // get current user
        User user = userService.getCurrentUser();

        // if this user already has a seller profile, throw error
        if (sellerRepository.existsByUser(user)) {
            throw new ValidationException("This user already has a seller profile");
        }

        // if the requested store name already exists, throw error
        if (sellerRepository.existsByStoreName(request.storeName())) {
            throw new ValidationException(("Store name is already taken"));
        }

        // add new SELLER role to current user's list of roles and save user
        user.getRoles().add(Role.SELLER);
        userRepository.save(user);

        // create a new Seller Profile for this user
        SellerProfile sellerProfile = new SellerProfile(user, request.storeName(), request.description());

        // save this seller profile into database
        return sellerRepository.save(sellerProfile);

    }

    /**
     * Returns the seller profile for the current authenticated user
     */
    public SellerProfile getSellerProfile() {
        // get current user
        User user = userService.getCurrentUser();

        // return seller profile if exists by user
        return sellerRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Seller profile cannot be found for current user"));
    }

    /**
     * Finds a seller profile by user id
     */
    public SellerProfile findById(Long id) {
        // return seller profile if exists by id
        return sellerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Seller profile cannot be found for user with id: " + id));
    }

    /** 
     * Return all seller profiles
     */
    public List<SellerProfile> findAllSellers() {
        return sellerRepository.findAll();
    }

    /**
     * Updates the current seller profile
     */
    @Transactional
    public SellerProfile updateSellerProfile(UpdateSellerProfileRequest request) {
        // get current user's seller profile
        SellerProfile sellerProfile = getSellerProfile();

        // if the requested store name isn't null or blank
        if (request.storeName() != null && !request.storeName().isBlank()) {

            // check if the user is trying to change the store name
            boolean storeNameChanged = request.storeName().equals(sellerProfile.getStoreName());

            // if the requested store name already exists
            if (storeNameChanged && sellerRepository.existsByStoreName(request.storeName()))  {
                throw new ValidationException("Store name is already taken");
            }

            // set store name into seller profile
            sellerProfile.setStoreName(request.storeName());
        }

        // if the store description isn't null or blank
        if (request.description() != null && !request.description().isBlank()) {
            // set description into seller profile
            sellerProfile.setDescription(request.description());
        }

        // save seller profile into database
        return sellerRepository.save(sellerProfile);
    }

    /**
     * Deletes the current user's seller profile
     */
    @Transactional
    public boolean deleteSellerProfile() {
        // get current user's seller profile
        SellerProfile sellerProfile = getSellerProfile();

        // get current user
        User user = userService.getCurrentUser();

        // remove SELLER role from current user
        user.getRoles().remove(Role.SELLER);

        // save user into the database
        userRepository.save(user);

        // delete seller profile from database
        sellerRepository.delete(sellerProfile);

        return true;
    }

}
