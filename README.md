# Generic Spring Boot CRUD Application

This project implements a highly reusable and testable backend architecture for a Java-based web application.

## ⚡ Implemented Optimizations

The framework has been enhanced with several production-grade optimizations:

### 1. High-Performance Reflection Caching
- **Implementation**: The `MappingCache` stores record constructors and component metadata.
- **Benefit**: Drastically reduces the overhead of the `toRecord()` method, making entity-to-DTO conversion nearly as fast as manual mapping.

### 2. Service Layer Registry (Strategy Pattern)
- **Implementation**: The `@CrudResource` annotation now supports a `service()` parameter.
- **Benefit**: Allows you to override the generic `BaseService` with specialized business logic for specific entities without breaking the dynamic architecture.

### 3. Dynamic Query Specification
- **Implementation**: Integrated JPA Specifications with a `GenericSpecification` builder.
- **Benefit**: Enables instant filtering via URL parameters (e.g., `/api/v2/products?name=Pro`) for all registered resources.

### 4. Visual Metadata Explorer
- **Implementation**: New endpoint at `/api/v2/metadata`.
- **Benefit**: Provides a real-time inventory of all registered resources, facilitating front-end automation and API discovery.

## 🏗️ Architectural Separation (DLI)

The project follows a strict **Data-Logic-Interface** separation to ensure maximum maintainability and clean system design:

### 1. Data Layer (`com.example.crudapp.data`)
- **Responsibility**: Database schema, JPA mappings, and raw data access.
- **Key Components**: `BaseEntity`, `BaseRepository`, and concrete `@Entity` classes (e.g., `Product`).
- **Constraint**: This layer has no knowledge of API contracts or business workflows.

### 2. Logic Layer (`com.example.crudapp.logic`)
- **Responsibility**: Business rules, resource orchestration, and dynamic registration.
- **Key Components**: `BaseService`, `DynamicCrudManager`, and `ResourceMetadata`.
- **Constraint**: This is the "Engine" of the application. It acts as a bridge, transforming Data (Entities) into Interface (Records) through logical operations.

### 3. Interface Layer (`com.example.crudapp.api`)
- **Responsibility**: External API surface, HTTP request handling, and immutable data contracts.
- **Key Components**: `UniversalCrudController` and Java `records` (e.g., `ProductRecord`).
- **Constraint**: This layer is strictly immutable. It never interacts with the database directly; it only talks to the Logic layer.

### 2. Streamlined Architecture
- **Java Records**: DTOs are now implemented as `records` (e.g., `ProductRecord`), providing a concise and immutable data structure.
- **`@CrudResource` Annotation**: A unified annotation that handles all the heavy lifting. By adding `@CrudResource(path = "products", dto = ProductRecord.class)` to an entity, you automatically register:
    - A dynamic **Repository**.
    - A generic **Service**.
    - A unified **Controller** route at `/api/v2/products`.

### 🌳 Hierarchical Relationships
The framework natively supports tree-like structures (Parent-Child, Grandparent-Child) through custom annotations:
- **`@Parent`**: Marks a field (in `BaseEntity`) that points to a parent entity of the same type.
- **`@Children`**: Marks a collection that holds child entities.
- **Automatic Mapping**: If your Record DTO includes components named `parentId` or `grandparentId`, the `toRecord()` engine will automatically resolve and populate these from the hierarchy using reflection.
- **Helper Methods**: `getGrandparent()` and `getGrandchildren()` are available on all entities for easy traversal.

## School Project Mastery: System Design Features

This framework is an excellent candidate for a high-grade system design project because it demonstrates several advanced architectural patterns:

### 1. Layered Architecture & Generics
- The project clearly separates concerns into **Controller**, **Service**, and **Repository** layers.
- Use of **Java Generics** shows a high level of abstraction, allowing one code path to handle many different data types.

### 2. Reflection & Metadata-Driven Design
- By using custom annotations (`@CrudResource`), the system uses **Reflection** to build itself at runtime. This is how many industry-leading frameworks (like Spring itself) operate.

### 3. Design Patterns
- **Factory Method**: Used in the `DynamicCrudManager` to instantiate repositories and services.
- **Proxy/Registry Pattern**: The `DynamicCrudManager` acts as a registry for all application resources.
- **Template Method**: `BaseService` and `BaseController` provide a template for CRUD operations that sub-classes can optionally override.

### 4. Automated Testing
- The custom `@CrudTest` annotation and `BaseCrudIntegrationTest` demonstrate how to build a **Testing Framework** inside your application, ensuring that any new entity is automatically verified against a standard suite of tests.

## 🚀 Future Roadmap: Framework Evolution

To evolve this prototype into a production-ready Enterprise CRUD Engine, the following modules are planned:

### 🛡️ Phase 1: Security & Validation (High Priority)
- **Dynamic Role-Based Access Control (RBAC)**: Enhance `@CrudResource` to support `rolesAllowed = {"ADMIN", "USER"}` and integrate with Spring Security for automatic route protection.
- **JSR-303 Validation Engine**: Automatically trigger `@Valid` on Records within the `UniversalCrudController` and map constraint violations to clean JSON error responses.
- **Global Audit Logging**: Add `createdBy`, `updatedBy`, and `version` fields to `BaseEntity` with automatic population via JPA Listeners.

### 🌐 Phase 2: Observability & Documentation
- **Auto-Generated OpenAPI (Swagger)**: Integrate `springdoc-openapi` to dynamically document the virtual routes generated by `DynamicCrudManager`.
- **Global Exception Mapping**: Implement a `@RestControllerAdvice` to translate internal `RuntimeException`s (like `EntityNotFound`) into standardized RFC-7807 Problem Details for HTTP APIs.
- **Actuator Health Checks**: Custom health indicators to monitor the status of registered dynamic resources.

### ⚙️ Phase 3: Advanced Data Patterns
- **Strategy Pattern for Custom Logic**: Allow `@CrudResource(service = CustomProductService.class)` to override the generic `BaseService` when complex business rules are required.
- **Generic Specification API**: Implement a dynamic filtering system (e.g., `/api/v2/products?price_gt=100&name_like=Pro`) using JPA Specifications and Reflection.
- **Soft Deletes**: Support a `@Deleted` annotation on entities to toggle between hard and soft deletion logic in the `BaseService`.

### 🧪 Phase 4: Developer Experience (DX)
- **GraphQL Support**: Add a generic GraphQL resolver that exposes all `@CrudResource` entities through a single schema.
- **Event-Driven Hooks**: Implement `onBeforeSave` and `onAfterDelete` event listeners that developers can implement to hook into the dynamic lifecycle.

## How to Run
1. Ensure Java 17+ is installed.
2. Run the application using your IDE or Maven: `mvn spring-boot:run`.
3. The API will be available at `http://localhost:8080/api/products`.
4. H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`).
