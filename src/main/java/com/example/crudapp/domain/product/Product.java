package com.example.crudapp.domain.product;

import com.example.crudapp.core.BaseEntity;
import com.example.crudapp.core.annotations.CrudResource;
import jakarta.persistence.Entity;
import lombok.*;

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
