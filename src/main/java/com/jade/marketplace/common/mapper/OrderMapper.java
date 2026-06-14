package com.jade.marketplace.common.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jade.marketplace.common.dto.OrderDto;
import com.jade.marketplace.order.Order;
import com.jade.marketplace.order.OrderItem;

/**
 * Converts Order entities into OrderDto records
 * 
 * Mappers are created so GraphQL/API layers do not expose JPA entities directly
 */
@Component
public class OrderMapper {

    /**
     * Converts one Order entity into a OrderDto
     */
    public OrderDto toDto(Order order) {
        // if order is null
        if (order == null) {
            return null;
        }

        // get total number of order items
        Integer itemQuantity = order.getOrderItems().stream().mapToInt(OrderItem::getQuantity).sum();

        // return order DTO
        return new OrderDto(order.getId(), order.getBuyer().getId(), order.getBuyer().getEmail(), order.getOrderStatus(), order.getAmount(), itemQuantity, order.getCreatedAt());
    } 

    /**
     * Converts a list of Order entities into OrderDto records
     */
    public List<OrderDto> toDtoList(List<Order> orders) {
        return orders.stream().map(this::toDto).toList();
    }
    
}
