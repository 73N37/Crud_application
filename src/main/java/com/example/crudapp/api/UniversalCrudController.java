package com.example.crudapp.api;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.infrastructure.query.GenericSpecification;
import com.example.crudapp.logic.CrudSecurityService;
import com.example.crudapp.logic.DynamicCrudManager;
import com.example.crudapp.logic.MappingService;
import com.example.crudapp.logic.ResourceMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * [INTERFACE LAYER]
 * Fully autonomous Universal Controller.
 * Features: Generics, Interceptors, Rich Metadata, and RBAC.
 */
@RestController
@RequestMapping("/api/v2")
public class UniversalCrudController {

    private static final Logger log = LoggerFactory.getLogger(UniversalCrudController.class);
    private final DynamicCrudManager crudManager;
    private final MappingService mappingService;
    private final CrudSecurityService securityService;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    public UniversalCrudController(DynamicCrudManager crudManager, 
                                   MappingService mappingService, 
                                   CrudSecurityService securityService, 
                                   Validator validator, 
                                   ObjectMapper objectMapper) {
        this.crudManager = crudManager;
        this.mappingService = mappingService;
        this.securityService = securityService;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    /**
     * 🧠 META-PROGRAMMING: Deep Metadata Explorer
     * Returns schemas for all resources to auto-generate UI forms.
     */
    @GetMapping("/metadata")
    public Map<String, List<ResourceMetadata.FieldInfo>> getMetadata() {
        log.debug("🔍 Fetching global metadata for all resources");
        return crudManager.getResources().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getFields()
                ));
    }

    @GetMapping("/{resource}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getAll(
            @PathVariable String resource,
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        log.info("📥 Request: GET /api/v2/{} (params: {})", resource, params);
        ResourceMetadata metadata = getMetadataOrThrow(resource);
        securityService.checkAccess(metadata);

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification spec = new GenericSpecification(params);
        Page<? extends BaseEntity> entityPage = metadata.getService().findAll(spec, pageable);

        List<?> records = entityPage.getContent().stream()
                .map(entity -> mappingService.toRecord(entity))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(entityPage.getTotalElements()))
                .body(records);
    }

    @GetMapping("/{resource}/{id}")
    public ResponseEntity<?> getById(@PathVariable String resource, @PathVariable Long id) {
        log.info("📥 Request: GET /api/v2/{}/{}", resource, id);
        ResourceMetadata<?, ?> metadata = getMetadataOrThrow(resource);
        securityService.checkAccess(metadata);

        return (ResponseEntity<?>) metadata.getService().findById(id)
                .map(entity -> ResponseEntity.ok(mappingService.toRecord(entity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{resource}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> create(@PathVariable String resource, @RequestBody Map<String, Object> body) {
        log.info("📥 Request: POST /api/v2/{} (body: {})", resource, body);
        ResourceMetadata metadata = getMetadataOrThrow(resource);
        securityService.checkAccess(metadata);

        Object dto = mappingService.mapToRecord(body, metadata.getDtoClass());
        validate(dto);

        BaseEntity entity = (BaseEntity) objectMapper.convertValue(dto, metadata.getEntityClass());
        
        log.debug("🧠 Executing pre-create hooks for [{}]", resource);
        metadata.getInterceptor().beforeCreate(entity);
        
        BaseEntity savedEntity = (BaseEntity) metadata.getService().save(entity);
        
        log.debug("🧠 Executing post-create hooks for [{}]", resource);
        metadata.getInterceptor().afterCreate(savedEntity);

        return ResponseEntity.ok(mappingService.toRecord(savedEntity));
    }

    @PutMapping("/{resource}/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> update(@PathVariable String resource, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        log.info("📥 Request: PUT /api/v2/{}/{} (body: {})", resource, id, body);
        ResourceMetadata metadata = getMetadataOrThrow(resource);
        securityService.checkAccess(metadata);

        Object dto = mappingService.mapToRecord(body, metadata.getDtoClass());
        validate(dto);

        BaseEntity entity = (BaseEntity) objectMapper.convertValue(dto, metadata.getEntityClass());
        
        log.debug("🧠 Executing pre-update hooks for [{}]", resource);
        metadata.getInterceptor().beforeUpdate(entity);
        
        BaseEntity updatedEntity = (BaseEntity) metadata.getService().update(id, entity);
        
        log.debug("🧠 Executing post-update hooks for [{}]", resource);
        metadata.getInterceptor().afterUpdate(updatedEntity);

        return ResponseEntity.ok(mappingService.toRecord(updatedEntity));
    }

    @DeleteMapping("/{resource}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String resource, @PathVariable Long id) {
        log.info("📥 Request: DELETE /api/v2/{}/{}", resource, id);
        ResourceMetadata<?, ?> metadata = getMetadataOrThrow(resource);
        securityService.checkAccess(metadata);
        
        log.debug("🧠 Executing pre-delete hooks for [{}]", resource);
        metadata.getInterceptor().beforeDelete(id);
        
        metadata.getService().deleteById(id);
        
        log.debug("🧠 Executing post-delete hooks for [{}]", resource);
        metadata.getInterceptor().afterDelete(id);
        
        return ResponseEntity.noContent().build();
    }

    private ResourceMetadata<?, ?> getMetadataOrThrow(String resource) {
        ResourceMetadata<?, ?> metadata = crudManager.getMetadata(resource);
        if (metadata == null) {
            log.warn("⚠️ Attempted access to unregistered resource: [{}]", resource);
            throw new RuntimeException("Resource not found: " + resource);
        }
        return metadata;
    }

    private void validate(Object dto) {
        Set<ConstraintViolation<Object>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            log.warn("❌ Validation failed for [{}]: {} violations", dto.getClass().getSimpleName(), violations.size());
            throw new ConstraintViolationException(violations);
        }
    }
}
