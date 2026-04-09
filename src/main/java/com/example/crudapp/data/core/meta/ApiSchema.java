package com.example.crudapp.data.core.meta;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * [META DATA LAYER]
 * Registry for dynamic Zero-Code APIs.
 * Each row represents a fully functional CRUD API.
 */
@Entity
@Table(name = "api_registry")
public class ApiSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String path; // e.g., "orders", "customers"

    private String requiredRoles; // e.g., "ADMIN,USER"

    @OneToMany(mappedBy = "schema", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ApiField> fields = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getRequiredRoles() { return requiredRoles; }
    public void setRequiredRoles(String requiredRoles) { this.requiredRoles = requiredRoles; }
    public List<ApiField> getFields() { return fields; }
    public void setFields(List<ApiField> fields) { this.fields = fields; }
}
