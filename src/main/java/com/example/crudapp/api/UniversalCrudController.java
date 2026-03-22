package com.example.crudapp.api;

import com.example.crudapp.logic.DynamicCrudManager;
import com.example.crudapp.logic.ResourceMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [INTERFACE LAYER]
 * Single entry point for all CRUD operations via the dynamic engine.
 */
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class UniversalCrudController {

    private final DynamicCrudManager crudManager;

    @GetMapping("/{resource}")
    public List<?> getAll(@PathVariable String resource) {
        ResourceMetadata<?, ?> metadata = crudManager.getMetadata(resource);
        if (metadata == null) throw new RuntimeException("Resource not found");
        return metadata.getService().findAll().stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());
    }

    @GetMapping("/{resource}/{id}")
    public ResponseEntity<?> getById(@PathVariable String resource, @PathVariable Long id) {
        ResourceMetadata<?, ?> metadata = crudManager.getMetadata(resource);
        if (metadata == null) return ResponseEntity.notFound().build();
        return (ResponseEntity<?>) metadata.getService().findById(id)
                .map(entity -> ResponseEntity.ok(entity.toRecord()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{resource}")
    public Object create(@PathVariable String resource, @RequestBody Map<String, Object> body) {
        // Note: Real implementation would use an ObjectMapper to map 'body' to 'Entity'
        return "Resource creation logic orchestrated by Logic layer for: " + resource;
    }

    @DeleteMapping("/{resource}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String resource, @PathVariable Long id) {
        ResourceMetadata<?, ?> metadata = crudManager.getMetadata(resource);
        if (metadata != null) {
            metadata.getService().deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }
}
