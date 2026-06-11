package com.jade.marketplace.product;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

/**
 * GraphQL mutations for chaning product data
 */
@Controller
public class ProductMutation {

    private final ProductService productService;

    /**
     * Constructor
     */
    public ProductMutation(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Creates a product 
     */
    @MutationMapping
    public Product createProduct(@Argument @Valid CreateProductRequest request) {
        return productService.createProduct(request);
    }

    /**
     * Update a product
     */
    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument @Valid UpdateProductRequest request) {
        return productService.updateProduct(id, request);
    }

    /**
     * Delete a product
     */
    @MutationMapping
    public boolean deleteProduct(@Argument Long id) {
        return productService.deleteProduct(id);
    }
}
