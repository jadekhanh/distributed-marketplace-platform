package com.jade.marketplace.common.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product information returned to GraphQL clients.
 */
public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String categoryName,
        Long sellerId,
        String sellerName,
        Double averageRating,
        List<String> imageUrls
) {
}