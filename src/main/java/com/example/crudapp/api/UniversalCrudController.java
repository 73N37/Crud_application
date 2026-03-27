package com.example.crudapp.api;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.infrastructure.query.GenericSpecification;
import com.example.crudapp.logic.DynamicCrudManager;
import com.example.crudapp.logic.ResourceMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [INTERFACE LAYER]
 * Single entry point for all CRUD operations via the optimized dynamic engine.
 */
@RestController
@RequestMapping("/api/v2")
public class UniversalCrudController {

    private final DynamicCrudManager crudManager;
    private final ObjectMapper objectMapper;

    public UniversalCrudController(DynamicCrudManager crudManager, ObjectMapper objectMapper) {
        this.crudManager = crudManager;
        this.objectMapper = objectMapper;
    }

    /**
     * 🛠️ DX OPTIMIZATION: Metadata Explorer
     * Returns all registered resources to allow frontend auto-generation of forms/clients.
     */
    @GetMapping("/metadata")
    public Map<String, String> getMetadata() {
        return crudManager.getResources().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getDtoClass().getSimpleName()
                ));
    }

    /**
     * ⚡ PERFORMANCE OPTIMIZATION: Dynamic Filtering, Pagination, and Sorting
     * All query parameters are automatically mapped to database predicates.
     */
    @GetMapping("/{resource}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getAll(
            @PathVariable String resource,
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        ResourceMetadata metadata = crudManager.getMetadata(resource);
        if (metadata == null) throw new RuntimeException("Resource not found");

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification spec = new GenericSpecification(params);
        Page<? extends BaseEntity> entityPage = metadata.getService().findAll(spec, pageable);

        List<?> records = entityPage.getContent().stream()
                .map(entity -> entity.toRecord())
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(entityPage.getTotalElements()))
                .body(records);
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
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> create(@PathVariable String resource, @RequestBody Map<String, Object> body) {
        ResourceMetadata metadata = crudManager.getMetadata(resource);
        if (metadata == null) throw new RuntimeException("Resource not found");

        Object entity = objectMapper.convertValue(body, metadata.getEntityClass());
        BaseEntity savedEntity = metadata.getService().save((BaseEntity) entity);

        return ResponseEntity.ok(savedEntity.toRecord());
    }

    @PutMapping("/{resource}/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> update(@PathVariable String resource, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        ResourceMetadata metadata = crudManager.getMetadata(resource);
        if (metadata == null) throw new RuntimeException("Resource not found");

        Object entity = objectMapper.convertValue(body, metadata.getEntityClass());
        BaseEntity updatedEntity = metadata.getService().update(id, (BaseEntity) entity);

        return ResponseEntity.ok(updatedEntity.toRecord());
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
