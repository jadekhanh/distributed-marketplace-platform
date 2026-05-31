package com.jade.marketplace.category;

import jakarta.validation.constraints.NotBlank;

/**
 * Data required inside request  to create category
 * This is a record type because it only carries request data so we only need constructor and getters
 */
public record CreateCategoryRequest(

    // String name must not be blank and must be a string
    @NotBlank(message = "Catergory name is required")
    String name,

    // String description must not be blank and must be a string
    @NotBlank(message = "Category description is required")
    String description
) {
}