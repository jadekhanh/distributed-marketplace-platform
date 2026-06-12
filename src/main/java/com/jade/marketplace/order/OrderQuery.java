package com.jade.marketplace.order;

import java.util.List;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL query for order data
 */
@Controller
public class OrderQuery {

    private final OrderService orderService;

    /**
     * Constructor
     */
    public OrderQuery(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Returns an order by its ID
     */
    @QueryMapping
    public Order findById(Long id) {
        return orderService.findById(id);
    }

    /**
     * Returns current user's order history
     */
    @QueryMapping
    public List<Order> findAllOrders() {
        return orderService.findAllOrders();
    }
    
}
