package com.example.crudapp;

import com.example.crudapp.logic.DynamicCrudManager;
import com.example.crudapp.logic.ResourceDiscoveryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CrudApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrudApplication.class, args);
    }

    /**
     * 🧠 META-PROGRAMMING: Autonomous Startup
     * Uses Reflection API and Classpath Scanning to find all @CrudResource
     * annotated entities and register them automatically.
     */
    @Bean
    CommandLineRunner setup(DynamicCrudManager crudManager, ResourceDiscoveryService discoveryService) {
        return args -> {
            discoveryService.discoverResources("com.example.crudapp.data")
                    .forEach(crudManager::registerResource);
        };
    }
}
