package com.jade.marketplace.product;

import java.math.BigDecimal;
import java.util.List;

import com.jade.marketplace.category.Category;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

/**
 * Data required to request to update product
 * All fields are optional
 */
public record UpdateProductRequest (

    String name,

    String description,

    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    BigDecimal price,

    @Min(value = 0, message = "Product quantity cannot be negative")
    Integer quantity,

    Category category,

    List<String> urls

) {
    
}
