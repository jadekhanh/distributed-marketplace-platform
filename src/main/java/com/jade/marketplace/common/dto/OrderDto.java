package com.jade.marketplace.common.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order information returned to GraphQL clients.
 */
public record OrderDto(
        Long id,
        Long buyerId,
        String buyerEmail,
        String status,
        BigDecimal totalAmount,
        Integer totalItems,
        LocalDateTime createdAt
) {
}