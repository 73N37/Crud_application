package com.example.crudapp.data.core.meta;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * [DATA LAYER]
 * Records all mutations in the Zero-Code system for professional traceability.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // CREATE, UPDATE, DELETE
    private String resource; // e.g., "orders"
    private Long recordId;
    private String username;
    
    @Column(columnDefinition = "TEXT")
    private String oldData;
    
    @Column(columnDefinition = "TEXT")
    private String newData;
    
    private LocalDateTime timestamp;

    public AuditLog() { this.timestamp = LocalDateTime.now(); }

    // Getters and Setters
    public Long getId() { return id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOldData() { return oldData; }
    public void setOldData(String oldData) { this.oldData = oldData; }
    public String getNewData() { return newData; }
    public void setNewData(String newData) { this.newData = newData; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
