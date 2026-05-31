package com.jade.marketplace.category;

import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.springframework.stereotype.Service;

import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.exception.ValidationException;

import java.util.List;
import java.util.Set;

/**
 * Category service handles all logic for category
 * 
 * Responsibilities:
 * - Find category
 * - Create category
 * - Update category
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // constructor
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Create a new category
     */
    public Category createCategory(CreateCategoryRequest request) {

        // check if category is already created
        if (categoryRepository.existsByName(request.name())) {
            throw new ValidationException("Category already exists: " + request.name());
        }

        // create new category using information from request
        Category category = new Category(request.name(), request.description());

        // save and return new category into MySQL database using CategoryRepository
        return categoryRepository.save(category);
    }

    /**
     * Get all categories in the database
     */
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Find a category by its id
     */
    public Category findById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    /**
     * Updates category fields
     */
    public void updateCategory(Long id, UpdateCategoryRequest request) {
        // get category by its id
        Category category = findById(id);

        
    }
}