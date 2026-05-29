package com.jade.marketplace.common.constants;

/**
 * Central place for Kafka topic names
 */
public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String ORDER_PLACED = "order-placed";
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String INVENTORY_RESERVATION_FAILED = "inventory-reservation-failed";

    public static final String PAYMENT_PROCESSED = "payment-processed";
    public static final String PAYMENT_FAILED = "payment-failed";

    public static final String ORDER_CONFIRMED = "order-confirmed";
    public static final String ORDER_CANCELLED = "order-cancelled";

    public static final String PRODUCT_UPDATED = "product-updated";
    public static final String REVIEW_ADDED = "review-added";

    public static final String NOTIFICATION_CREATED = "notification-created";

    public static final String DEAD_LETTER = "marketplace-dlq";
}