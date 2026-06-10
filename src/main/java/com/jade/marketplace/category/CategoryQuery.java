package com.jade.marketplace.category;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL queries related to categories
 * Queries read data
 * 
 * Note: GraphQL has 2 operations: query and mutation
 * 1. Query only reads
 * 2. Mutation does everything else
 */
@Controller
public class CategoryQuery {

    // CategoryService handles all logic related to categories
    private final CategoryService categoryService;

    public CategoryQuery(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Returns all categories using CategoryService's findAllCategories()
     * Requires the request to include:
     * Authorization: Bearer <jwt-token>
     */
    @QueryMapping
    public List<Category> categories() {
        return categoryService.findAllCategories();
    }

    /**
     * Returns a category by its id using CategoryService's findById()
     * Requires the request to include:
     * Authorization: Bearer <jwt-token>
     */
    @QueryMapping
    public Category category(@Argument Long id) {
        return categoryService.findById(id);
    }
}