package com.example.crudapp;

import com.example.crudapp.core.DynamicCrudManager;
import com.example.crudapp.domain.product.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CrudApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrudApplication.class, args);
    }

    @Bean
    CommandLineRunner setup(DynamicCrudManager crudManager) {
        return args -> {
            crudManager.registerResource(Product.class);
        };
    }
}
