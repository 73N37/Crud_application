package com.example.crudapp.data;

import com.example.crudapp.api.records.ProductRecord;
import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.infrastructure.annotations.CrudResource;
import jakarta.persistence.Entity;
import lombok.*;

/**
 * [DATA LAYER]
 * Database representation of a Product.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CrudResource(path = "products", dto = ProductRecord.class)
public class Product extends BaseEntity {
    private String name;
    private String description;
    private Double price;
}
