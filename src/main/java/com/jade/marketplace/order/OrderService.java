package com.jade.marketplace.order;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jade.marketplace.cart.Cart;
import com.jade.marketplace.cart.CartItem;
import com.jade.marketplace.cart.CartService;
import com.jade.marketplace.exception.OrderProcessingException;
import com.jade.marketplace.exception.ResourceNotFoundException;
import com.jade.marketplace.inventory.InventoryService;
import com.jade.marketplace.kafka.events.OrderCancelledEvent;
import com.jade.marketplace.kafka.events.OrderPlacedEvent;
import com.jade.marketplace.kafka.producer.OrderEventProducer;
import com.jade.marketplace.user.User;
import com.jade.marketplace.user.UserService;

import jakarta.transaction.Transactional;

/**
 * Order service handles all logic related to order
 * 
 * Responsibilities:
 * Create an order from cart
 * Reserve inventory
 * Publish Kafka order events
 * Query order history
 */
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final OrderEventProducer orderEventProducer;

    /**
     * Constructor
     */
    public OrderService(OrderRepository orderRepository, CartService cartService, UserService userService, InventoryService inventoryService, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.orderEventProducer = orderEventProducer;
    }

    /**
     * Get total amount from the cart
     */
    public BigDecimal calculateOrderTotal(Cart cart) {
        return cartService.calculateCartTotal();
    }

    /**
     * Find a buyer's order by order ID
     */
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cannot find order by its id: " + id));
    }

    /**
     * Get a buyer's order history
     * Note: we do not pass User as a parameter so that client cannot request another user's order history
     * User is fetched from the server via Spring Security
     * Server will find User based on JWT. Client does not get to choose
     */
    public List<Order> findAllOrders() {
        // get user from the server
        User user = userService.getCurrentUser();

        // get all orders
        return orderRepository.findByBuyerOrderByCreatedAtDesc(user);
    }

    /**
     * Publish a order-placed Kafka event per saved order
     */
    private void publishOrderPlacedEvents(Order order) {
        // for each order item in the order
        for (OrderItem item : order.getOrderItems()) {
            // create an order placed
            OrderPlacedEvent event = new OrderPlacedEvent(item.getId(), item.getProduct().getId(), item.getQuantity());

            // send a Kafka event
            orderEventProducer.publishOrderPlaced(event);
        }
    }

    /**
     * Publish a order-cancelled Kafka event per cancelled order
     */
    private void publishOrderCancelledEvents(Order order) {
        // create an order cancellation
        OrderCancelledEvent event = new OrderCancelledEvent(order.getId(), order.getBuyer().getId(), "Order cancelled by user");

        // send Kafka event
        orderEventProducer.publishOrderCancelled(event);
    }


    /**
     * Places an order using items inside cart
     * 
     * Flow: / what 
     * Load current user and cart
     * Validate cart is not empty
     * Reserve inventory for every cart item
     * Create order and order items
     * Publish Kafka event
     * Clear cart
     * 
     * Note: we do not pass User as a parameter so that client cannot request another user's order history
     * Note: we do not pass Cart as a parameter so that client cannot request another user's cart
     * Server will find User and Cart based on JWT. Client does not get to choose
     * 
     * @Transactional = to indicate that in this method, if something fails, everything must be rolled back. (e.g., if payment fails, then inventory must be rolled back and does not get deducted)
     */
    @Transactional
    public Order placeOrder() {
        // get buyer from the server
        User user = userService.getCurrentUser();

        // get cart from the server
        Cart cart = cartService.getCart();

        // validate cart is not empty
        if (cart.getItems().isEmpty()) {
            throw new OrderProcessingException("Cannot place an order with an empty cart!");
        }

        // get total amount from cart
        BigDecimal totalAmount = calculateOrderTotal(cart);

        // create a new order
        Order order = new Order(user, totalAmount);

        // for every cart item inside cart
        for (CartItem cartItem : cart.getItems()) {
            // reserve inventory for each cart item
            inventoryService.reserveInventory(cartItem.getId(), cartItem.getQuantity());

            // create a new order item for each cart item
            OrderItem orderItem = new OrderItem(cartItem.getProduct(), cartItem.getQuantity(), cartItem.getProduct().getPrice());

            // add order item into order
            order.addItem(orderItem);
        }

        // set order status
        order.setOrderStatus(OrderStatus.CONFIRMED);

        // save the order
        Order savedOrder = orderRepository.save(order);

        // publish order placed event
        publishOrderPlacedEvents(savedOrder);

        // clear cart
        cart.clearCart();

        // return saved order
        return savedOrder;
        
    }

    /**
     * Cancels an order by its ID by changing its status to CANCELLED
     */
    public Order cancelOrder(Long orderId) {
        // get order from ID
        Order order = findById(orderId);

        // set order status as CANCELLED
        order.setOrderStatus(OrderStatus.CANCELLED);

        // save the order
        Order savedOrder = orderRepository.save(order);

        // publish order cancelled Kafka event
        publishOrderCancelledEvents(savedOrder);

        // return saved order
        return savedOrder;
    }

}
