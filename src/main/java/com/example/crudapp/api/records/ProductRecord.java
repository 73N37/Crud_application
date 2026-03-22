package com.example.crudapp.api.records;

/**
 * [INTERFACE LAYER]
 * Immutable API representation of a Product, including hierarchy support.
 */
public record ProductRecord(
    String name,
    String description,
    Double price,
    Long parentId,
    Long grandparentId
) {}
