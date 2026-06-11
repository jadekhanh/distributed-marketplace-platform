package com.jade.marketplace.category;

import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQl mutations for changing category data
 */
@Controller
public class CategoryMutation {

    private final CategoryService categoryService;

    /**
     * Constructor of the controller
     * @param categoryService
     */
    public CategoryMutation(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Creates a new category
     */
    @MutationMapping
    public Category createCategory(@Argument @Valid CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    /**
     * Updates an existing category
     */
    @MutationMapping
    public Category updateCateory(@Argument Long id, @Argument UpdateCategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    /**
     * Deletes an existing category
     */
    @MutationMapping
    public void deleteCategory(@Argument Long id) {
        categoryService.deleteCategory(id);
    }
}