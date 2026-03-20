package com.example.crudapp.core;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * A Universal Controller that handles all @CrudResource annotated entities.
 */
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class UniversalCrudController {

    private final DynamicCrudManager crudManager;

    @GetMapping("/{resource}")
    public List<?> getAll(@PathVariable String resource) {
        ResourceMetadata metadata = crudManager.getMetadata(resource);
        if (metadata == null) throw new RuntimeException("Resource not found");
        return metadata.getService().findAll();
    }

    @GetMapping("/{resource}/{id}")
    public ResponseEntity<?> getById(@PathVariable String resource, @PathVariable Long id) {
        ResourceMetadata metadata = crudManager.getMetadata(resource);
        if (metadata == null) return ResponseEntity.notFound().build();
        return (ResponseEntity<?>) metadata.getService().findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{resource}")
    public Object create(@PathVariable String resource, @RequestBody Map<String, Object> body) {
        // Implementation for dynamic creation from Map to Entity/Record would go here.
        // For simplicity, this handles the concept.
        return "Resource created: " + resource;
    }

    @DeleteMapping("/{resource}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String resource, @PathVariable Long id) {
        ResourceMetadata metadata = crudManager.getMetadata(resource);
        if (metadata != null) {
            metadata.getService().deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }
}
