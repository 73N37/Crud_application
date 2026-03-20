package com.example.crudapp.domain.product;

import com.example.crudapp.factory.ResourceFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory implements ResourceFactory<Product, ProductDTO> {

    @Override
    public Product createEntity(ProductDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
    }
}
