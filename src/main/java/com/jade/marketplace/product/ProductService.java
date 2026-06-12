package com.jade.marketplace.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jade.marketplace.category.CategoryService;
import com.jade.marketplace.exception.ForbiddenException;
import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.seller.SellerProfile;
import com.jade.marketplace.seller.SellerService;

import jakarta.transaction.Transactional;

/**
 * Product service to handle all logics related to product
 * 
 * Responsibilities:
 * Create new product
 * Update existing product
 * Delete existing product
 * Search existing product
 */
@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final SellerService sellerService;

    /**
     * Constructor
     */
    public ProductService(ProductRepository productRepository, CategoryService categoryService, SellerService sellerService) {
        this.categoryService = categoryService;
        this.productRepository = productRepository;
        this.sellerService = sellerService;
    }

    /**
     * Finds a product by its id
     */
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    } 

    /**
     * Returns all products
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Returns all products in a category
     */
    public List<Product> findAllProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Returns all products owned by a seller profile
     */
    public List<Product> findAllProductsBySeller(Long sellerId) {
        return productRepository.findBySellerProfileId(sellerId);
    }

    /**
     * Searches products by a keyword
     */
    public List<Product> searchProducts(String keyword) {
        // if keyword is null or blank, return every product
        if (keyword == null || keyword.isBlank()) {
            return findAllProducts();
        }

        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Ensures that the current user is the product owner before mutating products
     */
    private void validateProductOwner(Product product, SellerProfile sellerProfile) {
        // get seller profile from product
        SellerProfile productSellerProfile= product.getSellerProfile();

        // compare seller profile with the seller profile from product
        if (!sellerProfile.equals(productSellerProfile)) {
            throw new ForbiddenException("You do not have permission to mofidy this product");
        }
    } 

    /**
     * Add images into product
     */
    private void addImages(Product product, List<String> urls) {
        for (String url : urls) {
            // add new ProductImage objects into list of product images
            product.addImage(new ProductImage(product, url));
        }
    }

    /**
     * Creates a product
     */
    public Product createProduct(CreateProductRequest request) {
        // get user's seller profile
        SellerProfile sellerProfile = sellerService.getSellerProfile();

        // create a new product
        Product product = new Product(sellerProfile, request.name(), request.description(), request.category(), request.price(), request.quantity());

        // add images to the new product
        addImages(product, request.url());

        // return the saved product into repository
        return productRepository.save(product);
    }

    /**
     * Updates a product
     * Only the seller who owns this product can perform this action
     */
    @Transactional
    public Product updateProduct(Long productId, UpdateProductRequest request) {
        // get user's seller profile
        SellerProfile sellerProfile = sellerService.getSellerProfile();

        // get product
        Product product = findById(productId);

        // check if the seller profile is the same seller profile of the product
        validateProductOwner(product, sellerProfile);

        // if request wants to change product name
        if (request.name() != null || !request.name().isBlank()) {
            product.setName(request.name());
        }

        // if request wants to change description
        if (request.description() != null || !request.description().isBlank()) {
            product.setDescription(request.description());
        }

        // if request wants to change price
        if (request.price() != null) {
            product.setPrice(request.price());
        }

        // if request wants to change quantity
        if (request.quantity() != null) {
            product.setQuantity(request.quantity());
        }

        // if request wants to change category
        if (request.category() != null) {
            product.setCategory(request.category());
        }

        // if request wants to update product images
        if (request.urls() != null) {
            // clear all existing images
            product.removeImages();;
            
            // add new set of images
            addImages(product, request.urls());
        }

        // return a saved product into product repository
        return productRepository.save(product);
    }

    /**
     * Delete a product
     * Only the seller who owns the product can perform this action
     */
    @Transactional
    public boolean deleteProduct(Long productId) {
        // get user's seller profile
        SellerProfile sellerProfile = sellerService.getSellerProfile();

        // get product
        Product product = findById(productId);

        // check if the seller profile matches the seller profile of this product
        validateProductOwner(product, sellerProfile);

        // delete the product from repository and return true
        productRepository.delete(product);
        return true;
    }
}
