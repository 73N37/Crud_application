package com.example.crudapp.api;

import com.example.crudapp.data.core.meta.ApiField;
import com.example.crudapp.data.core.meta.ApiSchema;
import com.example.crudapp.data.core.meta.ApiSchemaRepository;
import com.example.crudapp.logic.SchemaCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [META API]
 * The "Factory" for your Zero-Code APIs.
 * Use this to create new Entities at runtime.
 */
@RestController
@RequestMapping("/api/meta/schemas")
public class SchemaController {

    private static final Logger log = LoggerFactory.getLogger(SchemaController.class);
    private final ApiSchemaRepository schemaRepository;
    private final SchemaCacheService cacheService;

    public SchemaController(ApiSchemaRepository schemaRepository, SchemaCacheService cacheService) {
        this.schemaRepository = schemaRepository;
        this.cacheService = cacheService;
    }

    @GetMapping
    public List<ApiSchema> getAllSchemas() {
        return schemaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<ApiSchema> createNewEntity(@RequestBody ApiSchema schema) {
        log.info("🏗️ Creating new Zero-Code Entity at path: [/api/v3/{}]", schema.getPath());
        
        if (schema.getFields() != null) {
            schema.getFields().forEach(field -> field.setSchema(schema));
        }
        
        ApiSchema saved = schemaRepository.save(schema);
        cacheService.evict(saved.getPath());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Long id) {
        schemaRepository.findById(id).ifPresent(s -> cacheService.evict(s.getPath()));
        schemaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
