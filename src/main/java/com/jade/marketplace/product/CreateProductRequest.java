package com.jade.marketplace.product;

import java.math.BigDecimal;
import java.util.List;

import com.jade.marketplace.category.Category;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Data expected to request to create a new product
 */
public record CreateProductRequest (

    @NotBlank(message = "Product name is required")
    String name,

    @NotBlank(message = "Product description is required")
    String description,

    @NotBlank(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    BigDecimal price,

    @NotBlank(message = "Product quantity is required")
    @Min(value = 0, message = "Product quantity cannot be negative")
    Integer quantity,

    @NotBlank(message = "Product category is required")
    Category category,

    List<String> url

) {
    
}
