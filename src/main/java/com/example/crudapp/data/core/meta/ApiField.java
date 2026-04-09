package com.example.crudapp.data.core.meta;

import jakarta.persistence.*;

/**
 * [META DATA LAYER]
 * Dynamic rules for each field in your Zero-Code APIs.
 */
@Entity
@Table(name = "api_fields")
public class ApiField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "total_price"
    private String type; // e.g., "STRING", "NUMBER", "DATE", "RELATION"
    private String relationTo; // e.g., "customers" (if type is RELATION)
    private boolean required;
    private String regex; // for custom validation

    @ManyToOne
    @JoinColumn(name = "schema_id")
    private ApiSchema schema;

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRelationTo() { return relationTo; }
    public void setRelationTo(String relationTo) { this.relationTo = relationTo; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getRegex() { return regex; }
    public void setRegex(String regex) { this.regex = regex; }
    public ApiSchema getSchema() { return schema; }
    public void setSchema(ApiSchema schema) { this.schema = schema; }
}
