# Security Specification (Basic Authentication)

## Core Requirements
- **Framework:** Spring Security
- **Mechanism:** HTTP Basic Authentication
- **Credentials for Basic Auth:** 
  - RULE: User and password for BASIC AUTH are taken from environment variables `BASIC_AUTH_USERNAME`, `BASIC_AUTH_PASSWORD` (for spring.security.user.name and spring.security.user.password).
  - RULE: Do not store any sensitive information in the source code or version control. 

## Security Filter Chain Configuration
Create a security configuration class (`SecurityConfig.java`) that applies the following rules:
- **CSRF:** Disabled for all `/api/**` endpoints (stateless API posture).
- **Session Management:** Set to `SessionCreationPolicy.STATELESS`.
- **Authorization Rules:**
    - Permit all (no authentication required) for: /health, /swagger-ui, /v3/api-docs, /swagger-ui.html
    - All other `/api/v1/**` endpoints: Must be **authenticated**.
    - Throw 403 error for unauthenticated access attempts using error handling and structure of this project.

