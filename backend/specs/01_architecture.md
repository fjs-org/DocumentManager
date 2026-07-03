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

## Mapping
- **Object Mapping:** MapStruct for DTO to Entity and vice versa.

## Base Configuration
- **Package Structure:** `com.documentmanager`
    - `.controller` -> Web Layer
    - `.dto` -> DTOs Data Transfer Objects
      - use one and the same DTO definition for POST, PUT, GET operation - NOT a separate one for each operation
      - make use of AccessMode in DTO definition
      - give an example of each attribute in swagger UI
    - `.service` -> Business Logic (Each service is an interface with an implementation class)
    - `.repository` -> Database Access
    - `.model` -> JPA Entities
- **Configuration Files:** `application.yml` for environment-specific settings.
- **Swagger UI:** 
  - All endpoints are documented and accessible via Swagger UI at `/swagger-ui.html` (unauthenticated).
  - Use @Operation to describe each method in controllers. Also add @ApiResponse for each possible response code.

## Error Handling
- **Global Exception Handling:** Use `@ControllerAdvice` to handle exceptions globally.
- **Error response structure:** Consistent format, including:
    - `timestamp`
    - `status`
    - `error`
    - `message`
    - `path`

## Testing
- Create Unit test for controllers and services in respective test packages.
- Only have /health and /info unit tests in DocumentManagerApplicationTests.
- Use @WithMockUser(username = "admin") for authenticated endpoints in unit tests.