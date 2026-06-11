package com.jade.marketplace.product;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL queries for reading product data
 */
@Controller
public class ProductQuery {

    private final ProductService productService;

    /**
     * Constructor
     */
    public ProductQuery(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Return all products
     */
    @QueryMapping
    public List<Product> products() {
        return productService.findAllProducts();
    }
    
    /**
     * Return product by its ID
     */
    @QueryMapping
    public Product product(@Argument Long id) {
        return productService.findById(id);
    }

    /**
     * Return products under a specified category
     */
    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return productService.findAllProductsByCategory(categoryId);
    }

    /**
     * Return products owned by a seller
     */
    @QueryMapping
    public List<Product> productsBySeller(@Argument Long sellerId) {
        return productService.findAllProductsBySeller(sellerId);
    }

    /**
     * Searches products by name keyword
     */
    @QueryMapping
    public List<Product> searchProducts(@Argument String keyword) {
        return productService.searchProducts(keyword);
    }
    
}
