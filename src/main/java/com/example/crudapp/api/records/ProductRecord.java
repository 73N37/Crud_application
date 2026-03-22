package com.example.crudapp.api.records;

/**
 * [INTERFACE LAYER]
 * Immutable API representation of a Product.
 */
public record ProductRecord(
    String name,
    String description,
    Double price
) {}
