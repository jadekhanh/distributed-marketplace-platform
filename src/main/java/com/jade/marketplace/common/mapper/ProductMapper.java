package com.jade.marketplace.common.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jade.marketplace.common.dto.ProductDto;
import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductImage;

/**
 * Converts Product entities into ProductDto records
 * 
 * Mappers are created so GraphQL/API layers do not expose JPA entities directly
 */
@Component
public class ProductMapper {

    /**
     * Converts one Product entity into a ProductDto
     */
    public ProductDto toDto(Product product) {
        // if product is null
        if (product == null) {
            return null;
        }

        // get product images
        List<String> imageUrls = product.getImages().stream().map(ProductImage::getUrl).toList();

        // return product DTO
        return new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(), product.getCategory().getName(), product.getSellerProfile().getId(), null, imageUrls);
    } 

    /**
     * Converts a list of Product entities into ProductDto records
     */
    public List<ProductDto> toDtoList(List<Product> products) {
        return products.stream().map(this::toDto).toList();
    }

    
}
