package com.jade.marketplace.common.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jade.marketplace.order.OrderStatus;

/**
 * Order information returned to GraphQL clients.
 */
public record OrderDto(
        Long id,
        Long buyerId,
        String buyerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        Integer totalItems,
        LocalDateTime createdAt
) {
}