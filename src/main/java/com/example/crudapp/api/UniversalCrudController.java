package com.example.crudapp.api;

import com.example.crudapp.infrastructure.query.GenericSpecification;
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
@RequiredArgsConstructor
public class UniversalCrudController {

    private final DynamicCrudManager crudManager;

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
     * ⚡ PERFORMANCE OPTIMIZATION: Dynamic Filtering
     * All query parameters are automatically mapped to database predicates.
     */
    @GetMapping("/{resource}")
    @SuppressWarnings("unchecked")
    public List<?> getAll(@PathVariable String resource, @RequestParam Map<String, String> params) {
        ResourceMetadata metadata = crudManager.getMetadata(resource);
        if (metadata == null) throw new RuntimeException("Resource not found");
        
        Specification spec = new GenericSpecification(params);
        List<? extends BaseEntity> entities = metadata.getService().findAll(spec);
        
        return entities.stream()
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
