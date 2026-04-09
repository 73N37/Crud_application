package com.example.crudapp.data.core.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VirtualDocumentRepository extends JpaRepository<VirtualDocument, Long>, JpaSpecificationExecutor<VirtualDocument> {
    List<VirtualDocument> findByResourceType(String resourceType);
}
