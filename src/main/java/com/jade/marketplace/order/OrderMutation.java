package com.jade.marketplace.order;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL for mutating order data
 */
@Controller
public class OrderMutation {

    private final OrderService orderService;

    /**
     * Constructor
     */
    public OrderMutation(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Places an order from user's cart
     */
    @MutationMapping
    public Order placeOrder() {
        return orderService.placeOrder();
    }

    /**
     * Cancels an order
     */
    @MutationMapping
    public Order cancelOrder(@Argument Long id) {
        return orderService.cancelOrder(id);
    }

    
}
