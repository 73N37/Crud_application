package com.example.crudapp.logic;

import com.example.crudapp.data.core.meta.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * [PROFESSIONAL SERVICE LAYER]
 * Handles Caching, Ownership, Auditing, and Relationship Validation.
 */
@Service
public class VirtualCrudService {

    private static final Logger log = LoggerFactory.getLogger(VirtualCrudService.class);
    private final VirtualDocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;
    private final SchemaCacheService cacheService;
    private final CrudSecurityService securityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VirtualCrudService(VirtualDocumentRepository documentRepository, 
                              AuditLogRepository auditLogRepository,
                              SchemaCacheService cacheService,
                              CrudSecurityService securityService) {
        this.documentRepository = documentRepository;
        this.auditLogRepository = auditLogRepository;
        this.cacheService = cacheService;
        this.securityService = securityService;
    }

    @Transactional(readOnly = true)
    public List<VirtualDocument> getAll(String resourceType) {
        ApiSchema schema = getAndCheckSchema(resourceType);
        return documentRepository.findByResourceType(resourceType);
    }

    @Transactional
    public VirtualDocument save(String resourceType, Map<String, Object> data) {
        ApiSchema schema = getAndCheckSchema(resourceType);
        validateData(schema, data);

        VirtualDocument doc = new VirtualDocument();
        doc.setResourceType(resourceType);
        doc.setData(data);
        doc.setOwner(getCurrentUser());
        
        VirtualDocument saved = documentRepository.save(doc);
        audit("CREATE", resourceType, saved.getId(), null, data);
        return saved;
    }

    @Transactional
    public VirtualDocument update(Long id, Map<String, Object> data) {
        VirtualDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        
        checkOwnership(doc);
        ApiSchema schema = getAndCheckSchema(doc.getResourceType());
        validateData(schema, data);
        
        Map<String, Object> oldData = doc.getData();
        doc.setData(data);
        VirtualDocument saved = documentRepository.save(doc);
        
        audit("UPDATE", doc.getResourceType(), id, oldData, data);
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        VirtualDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        
        checkOwnership(doc);
        getAndCheckSchema(doc.getResourceType());
        
        audit("DELETE", doc.getResourceType(), id, doc.getData(), null);
        documentRepository.deleteById(id);
    }

    public Optional<VirtualDocument> getById(String resourceType, Long id) {
        ApiSchema schema = getAndCheckSchema(resourceType);
        return documentRepository.findById(id);
    }

    private ApiSchema getAndCheckSchema(String resourceType) {
        ApiSchema schema = cacheService.getSchema(resourceType)
                .orElseThrow(() -> new RuntimeException("API not registered: " + resourceType));
        securityService.checkAccess(schema);
        return schema;
    }

    private void checkOwnership(VirtualDocument doc) {
        String currentUser = getCurrentUser();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !doc.getOwner().equals(currentUser)) {
            log.warn("⛔ User [{}] attempted to mutate document owned by [{}]", currentUser, doc.getOwner());
            throw new RuntimeException("Access Denied: You do not own this record.");
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void validateData(ApiSchema schema, Map<String, Object> data) {
        for (ApiField field : schema.getFields()) {
            Object value = data.get(field.getName());
            
            if (field.isRequired() && value == null) {
                throw new RuntimeException("Field is required: " + field.getName());
            }

            // ⛓️ RELATIONSHIP VALIDATION
            if (value != null && "RELATION".equals(field.getType())) {
                validateRelation(field.getRelationTo(), value);
            }

            if (value != null && field.getRegex() != null && !field.getRegex().isEmpty()) {
                if (!String.valueOf(value).matches(field.getRegex())) {
                    throw new RuntimeException("Field pattern mismatch: " + field.getName());
                }
            }
        }
    }

    private void validateRelation(String targetResource, Object targetId) {
        log.debug("⛓️ Validating relationship to [{}] ID [{}]", targetResource, targetId);
        Long id = Long.valueOf(String.valueOf(targetId));
        if (!documentRepository.existsById(id)) {
            throw new RuntimeException("Related " + targetResource + " record not found: " + id);
        }
    }

    private void audit(String action, String resource, Long recordId, Map<String, Object> oldData, Map<String, Object> newData) {
        try {
            AuditLog log = new AuditLog();
            log.setAction(action);
            log.setResource(resource);
            log.setRecordId(recordId);
            log.setUsername(getCurrentUser());
            log.setOldData(oldData != null ? objectMapper.writeValueAsString(oldData) : null);
            log.setNewData(newData != null ? objectMapper.writeValueAsString(newData) : null);
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to write audit log", e);
        }
    }
}
