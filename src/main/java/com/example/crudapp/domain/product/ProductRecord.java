package com.example.crudapp.domain.product;

/**
 * Product DTO as a Java Record.
 */
public record ProductRecord(
    String name,
    String description,
    Double price
) {}
