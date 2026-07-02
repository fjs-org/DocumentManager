# System Architecture Specification

## Project Initialization
- **Framework:** Spring Boot 4.1.0
- **Language:** Java 25
- **Build Tool:** Maven (pom.xml)
- **Packaging:** Jar

## Core Dependencies
- Spring Web

## Base Configuration
- **Package Structure:** `com.documentmanager`
    - `.controller` -> Web Layer
    - `.service` -> Business Logic
    - `.repository` -> Database Access
    - `.model` -> JPA Entities
    - `.dto` -> DTOs Data Transfer Objects
- **Configuration Files:** `application.yaml` for environment-specific settings.
