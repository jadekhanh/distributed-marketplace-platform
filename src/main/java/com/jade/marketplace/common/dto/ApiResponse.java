package com.jade.marketplace.common.dto;

import java.time.LocalDateTime;

/**
 * Standard API response returned by mutations and service operations.
 * Example:
 * {
 *   "success": true,
 *   "message": "Product created successfully",
 *   "timestamp": "2026-05-28T20:15:00"
 * }
 */
public record ApiResponse(
        boolean success,
        String message,
        LocalDateTime timestamp
) {
    /**
     * Convenience factory method for successful responses.
     */
    public static ApiResponse success(String message) {
        return new ApiResponse(
                true,
                message,
                LocalDateTime.now()
        );
    }
    /**
     * Convenience factory method for failure responses.
     */
    public static ApiResponse failure(String message) {
        return new ApiResponse(
                false,
                message,
                LocalDateTime.now()
        );
    }
}