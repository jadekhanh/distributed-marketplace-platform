package com.jade.marketplace.category;

/**
 * Data required inside request to update category
 * 
 * This is a record type instead of class since it only carries request data so we only need to have a constructor and setters
 */
public record UpdateCategoryRequest(

    // this field can be optional
    String name,

    // this field can be optional
    String description
) {
}