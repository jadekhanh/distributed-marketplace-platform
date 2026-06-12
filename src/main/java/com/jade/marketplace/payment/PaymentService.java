package com.jade.marketplace.payment;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.jade.marketplace.common.constants.KafkaTopics;
import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.exception.ValidationException;
import com.jade.marketplace.order.Order;
import com.jade.marketplace.order.OrderService;
import com.jade.marketplace.order.OrderStatus;

import jakarta.transaction.Transactional;

/**
 * Payment service handles all logic related to payment
 */
@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Constructor
     */
    public PaymentService(PaymentRepository paymentRepository, OrderService orderService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Find a payment by its ID
     */
    public Payment findById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Cannot find payment with id: " + paymentId));
    }

    /**
     * Find a payment by its order ID
     */
    public Payment findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() -> new ResourceNotFoundException("Cannot find payment with order id: " + orderId));
    }

    /**
     * Create a transaction ID for a payment
     */
    public String createTransactionId() {
        return "helloplushies" + UUID.randomUUID();
    }

    /**
     * Create a refund transaction ID for a refund
     */
    public String createRefundTransactionId() {
        return "byeplushies" + UUID.randomUUID();
    }

    /**
     * Create payment for an order
     * 
     * Flow:
     * Find order
     * Prevent duplicate payments
     * Create payment in PENDING state
     * Simulate successful payment provider response
     * Mark payment completed
     * Confirm order
     * Publish Kafka event
     * 
     * @Transactional = to indicate that in this method, if something fails, everything must be rolled back
     */
    @Transactional
    public Payment createPayment(CreatePaymentRequest request) {
        // get order
        Order order = orderService.findById(request.orderId());

        // check if this order already has a payment already
        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new ValidationException("Payment already exists for this order id: " + order.getId());
        }

        // create a new payment
        Payment payment = new Payment(order, order.getAmount());

        // create a transaction ID
        String transactionID = createTransactionId();

        // mark payment as COMPLETED
        payment.markCompleted(transactionID);

        // save payment
        Payment savedPayment = paymentRepository.save(payment);

        // mark order as CONFIRMED
        order.setOrderStatus(OrderStatus.CONFIRMED);

        // send a Kafka event
        kafkaTemplate.send(KafkaTopics.PAYMENT_PROCESSED, new PaymentProcessedEvent(savedPayment.getId(), order.getId(), savedPayment.getAmount()));

        // return saved payment
        return savedPayment;
        
    }

    /**
     * Refunds a completed payment
     */
    @Transactional
    public Payment refundPayment(Long paymentId) {
        // get payment
        Payment payment = findById(paymentId);

        // only refund payment that is already COMPLETED
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new ValidationException("Only refund payment that is already completed");
        }

        // create a refund transaction ID
        String refundTransactionId = createRefundTransactionId();

        // mark payment as REFUNDED
        payment.markRefunded(refundTransactionId);

        // return saved refunded payment
        return paymentRepository.save(payment);

    }

}
