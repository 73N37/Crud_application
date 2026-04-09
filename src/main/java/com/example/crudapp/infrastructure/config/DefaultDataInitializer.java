package com.example.crudapp.infrastructure.config;

import com.example.crudapp.data.core.meta.ApiField;
import com.example.crudapp.data.core.meta.ApiSchema;
import com.example.crudapp.data.core.meta.ApiSchemaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with sample Zero-Code APIs to demonstrate different roles.
 */
@Component
public class DefaultDataInitializer implements CommandLineRunner {

    private final ApiSchemaRepository schemaRepository;

    public DefaultDataInitializer(ApiSchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    @Override
    public void run(String... args) {
        if (schemaRepository.count() > 0) return;

        // 1. Admin-Only Resource
        ApiSchema adminSchema = new ApiSchema();
        adminSchema.setPath("salaries");
        adminSchema.setRequiredRoles("ADMIN");
        
        ApiField f1 = new ApiField();
        f1.setName("amount"); f1.setType("NUMBER"); f1.setRequired(true); f1.setSchema(adminSchema);
        adminSchema.setFields(List.of(f1));
        schemaRepository.save(adminSchema);

        // 2. User/Admin Resource
        ApiSchema userSchema = new ApiSchema();
        userSchema.setPath("tasks");
        userSchema.setRequiredRoles("USER,ADMIN");
        
        ApiField f2 = new ApiField();
        f2.setName("title"); f2.setType("STRING"); f2.setRequired(true); f2.setSchema(userSchema);
        userSchema.setFields(List.of(f2));
        schemaRepository.save(userSchema);

        // 3. Public Resource (ANYONE)
        ApiSchema publicSchema = new ApiSchema();
        publicSchema.setPath("news");
        publicSchema.setRequiredRoles("ANYONE");
        
        ApiField f3 = new ApiField();
        f3.setName("headline"); f3.setType("STRING"); f3.setRequired(true); f3.setSchema(publicSchema);
        publicSchema.setFields(List.of(f3));
        schemaRepository.save(publicSchema);
    }
}
