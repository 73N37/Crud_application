package com.example.crudapp.logic;

import com.example.crudapp.data.core.meta.ApiSchema;
import com.example.crudapp.infrastructure.annotations.CrudResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * [LOGIC LAYER]
 * Unified Security Service for both Annotated (v2) and Zero-Code (v3) resources.
 */
@Service
public class CrudSecurityService {

    private static final Logger log = LoggerFactory.getLogger(CrudSecurityService.class);

    /**
     * 🛡️ V2 Check: Annotated Entities
     */
    public void checkAccess(ResourceMetadata<?, ?> metadata) {
        Class<?> entityClass = metadata.getEntityClass();
        if (!entityClass.isAnnotationPresent(CrudResource.class)) return;

        CrudResource annotation = entityClass.getAnnotation(CrudResource.class);
        String[] requiredRoles = annotation.roles();
        validate(requiredRoles, metadata.getBasePath());
    }

    /**
     * 🛡️ V3 Check: Zero-Code Schema
     */
    public void checkAccess(ApiSchema schema) {
        String rolesString = schema.getRequiredRoles();
        if (rolesString == null || rolesString.isBlank()) return;
        
        String[] requiredRoles = rolesString.split(",");
        validate(requiredRoles, schema.getPath());
    }

    private void validate(String[] requiredRoles, String resourceName) {
        if (requiredRoles.length == 0) return;
        
        // Check for 'ANYONE' role (Public access)
        if (Arrays.asList(requiredRoles).contains("ANYONE")) {
            log.debug("🔓 Public access granted for resource: [{}]", resourceName);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("⛔ Anonymous user denied access to: [{}]", resourceName);
            throw new RuntimeException("Authentication required");
        }

        boolean hasRole = Arrays.stream(requiredRoles)
                .map(String::trim)
                .anyMatch(role -> auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_" + role)));

        if (!hasRole) {
            log.warn("⛔ User [{}] denied access to resource: [{}] (Required: {})", 
                    auth.getName(), resourceName, Arrays.toString(requiredRoles));
            throw new RuntimeException("Access denied: missing required role(s) " + Arrays.toString(requiredRoles));
        }
    }
}
