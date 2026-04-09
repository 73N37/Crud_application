package com.example.crudapp.data;

import com.example.crudapp.api.records.ProductRecord;
import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.infrastructure.annotations.CrudResource;
import jakarta.persistence.Entity;

/**
 * [DATA LAYER]
 * Database representation of a Product.
 */
@Entity
@CrudResource(path = "products", dto = ProductRecord.class, roles = {"ADMIN", "USER"})
public class Product extends BaseEntity {
    private String name;
    private String description;
    private Double price;

    public Product() {}

    public Product(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private String name;
        private String description;
        private Double price;

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public Product build() {
            return new Product(name, description, price);
        }
    }
}
