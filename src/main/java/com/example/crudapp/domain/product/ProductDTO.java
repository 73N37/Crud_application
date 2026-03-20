package com.example.crudapp.domain.product;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private String description;
    private Double price;
}
