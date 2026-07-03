# System Architecture Specification

## Project Initialization
- **Framework:** Spring Boot 4.1.0
- **Language:** Java 25
- **Build Tool:** Maven (pom.xml)
- **Packaging:** Jar

## Core Dependencies
- Spring Web
- Lombok
- Use SL4J for logging
- Spring Data JPA
- H2 Database (for development and testing)

## Base package structure
- **Package Structure:** `com.documentmanager`
    - `.config` → Security, application configuration beans                                                                                                                                      
    - `.controller` -> Web Layer
    - `.dto` -> DTOs Data Transfer Objects
      - RULE: use one and the same DTO definition for POST, PUT, GET operation - NOT a separate one for each operation
      - make use of AccessMode in DTO definition
      - give an example of each attribute in swagger UI
    - `.exception` → Custom exceptions and global error handler
    - `.mapper` → MapStruct mapper interfaces
      - RULE: Use MapStruct for mapping DTO to Entity and vice versa.
    - `.model` -> JPA Entities
    - `.repository` -> Database Access
    - `.service` -> Business Logic (Each service is an interface with an implementation class) 
- **Configuration Files:** `application.yml` for environment-specific settings.
- **Swagger UI:** 
  - All endpoints are documented and accessible via Swagger UI at `/swagger-ui.html` (unauthenticated).
  - Use @Operation to describe each method in controllers. Also add @ApiResponse for each possible response code.

## Implementation Order
1. Model entities (User, Document) → model/
2. Repository interfaces → repository/
3. DTO + Mapper → dto/, mapper/
4. Service interface + impl → service/
5. Controller → controller/
6. Exception handling → exception/
7. Security config → config/
8. Tests → test/

## Error Handling
- **Global Exception Handling:** Use `@ControllerAdvice` to handle exceptions globally.
- **Error response structure:** Consistent format, including:
    - `timestamp`
    - `status`
    - `error`
    - `message`
    - `path`

## Testing standards
- Create Unit test for controllers and services in respective test packages.
- Only have /health and /info unit tests in DocumentManagerApplicationTests.
- Use @WithMockUser(username = "admin") for authenticated endpoints in unit tests.