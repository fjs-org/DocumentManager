# Sequence Diagrams

## POST /api/v1/users (Authenticated)

```mermaid
sequenceDiagram
    participant Client
    participant SecurityFilter as Security Filter Chain
    participant UserController
    participant UserService
    participant UserRepository
    participant UserMapper
    participant DB as H2 Database

    Client->>SecurityFilter: POST /api/v1/users<br/>Authorization: Basic xxx
    SecurityFilter->>SecurityFilter: Authenticate credentials
    SecurityFilter->>UserController: Forward request

    UserController->>UserService: createUser(UserDto)
    UserService->>UserRepository: existsByEmail(email)
    UserRepository->>DB: SELECT COUNT(*) WHERE email=?
    DB-->>UserRepository: 0
    UserService->>UserMapper: toEntity(UserDto)
    UserMapper-->>User: User entity
    UserService->>UserRepository: save(User)
    UserRepository->>DB: INSERT INTO users
    DB-->>UserRepository: saved User with id
    UserService->>UserMapper: toDto(savedUser)
    UserMapper-->>UserDto: DTO with id + createdAt
    UserService-->>UserController: UserDto
    UserController-->>Client: 201 Created<br/>{id, email, fullName, createdAt}
```

## POST /api/v1/users - Duplicate Email

```mermaid
sequenceDiagram
    participant Client
    participant SecurityFilter as Security Filter Chain
    participant UserController
    participant UserService
    participant UserRepository
    participant GlobalHandler as GlobalExceptionHandler
    participant DB as H2 Database

    Client->>SecurityFilter: POST /api/v1/users<br/>email: existing@example.com
    SecurityFilter->>UserController: Forward request

    UserController->>UserService: createUser(UserDto)
    UserService->>UserRepository: existsByEmail(email)
    UserRepository->>DB: SELECT COUNT(*) WHERE email=?
    DB-->>UserRepository: 1
    UserService--xUserController: throw EmailAlreadyExistsException

    UserController--xGlobalHandler: Exception
    GlobalHandler->>GlobalHandler: handleEmailAlreadyExists()
    GlobalHandler-->>Client: 400 Bad Request<br/>{status:400, error:"Bad Request",<br/>message:"Email already exists: ...", path:"/users"}
```

## POST /api/v1/users - Unauthenticated

```mermaid
sequenceDiagram
    participant Client
    participant SecurityFilter as Security Filter Chain
    participant EntryPoint as AuthenticationEntryPoint
    participant ErrResp as ErrorResponse

    Client->>SecurityFilter: POST /api/v1/users<br/>No Authorization header
    SecurityFilter->>SecurityFilter: Reject - no credentials
    SecurityFilter->>EntryPoint:.commence(request, response)
    EntryPoint->>ErrResp: new ErrorResponse(403, "Forbidden", "Access Denied", path)
    EntryPoint->>EntryPoint: Serialize to JSON, write response
    EntryPoint-->>Client: 403 Forbidden<br/>{status:403, error:"Forbidden",<br/>message:"Access Denied", path:"/users"}
```

## GET /health (Public)

```mermaid
sequenceDiagram
    participant Client
    participant SecurityFilter as Security Filter Chain
    participant Actuator as Spring Actuator

    Client->>SecurityFilter: GET /health
    SecurityFilter->>SecurityFilter: Permit all (matches /health)
    SecurityFilter->>Actuator: Forward request
    Actuator-->>Client: 200 OK<br/>{status:"UP"}
```

## GET /api/v1/users/{id}

```mermaid
sequenceDiagram
    participant Client
    participant SecurityFilter as Security Filter Chain
    participant UserController
    participant UserService
    participant UserRepository
    participant DB as H2 Database

    Client->>SecurityFilter: GET /api/v1/users/{id}
    SecurityFilter->>SecurityFilter: Authenticate credentials
    SecurityFilter->>UserController: Forward request

    UserController->>UserService: getUserById(UUID)
    UserService->>UserRepository: findById(UUID)
    UserRepository->>DB: SELECT * FROM users WHERE id=?
    alt User Found
        DB-->>UserRepository: User row
        UserService->>UserService: toDto(user)
        UserService-->>UserController: UserDto
        UserController-->>Client: 200 OK<br/>{id, email, fullName, createdAt}
    else User Not Found
        DB-->>UserRepository: empty
        UserService--xUserController: throw UserNotFoundException
        UserController--xGlobalHandler: Exception
        GlobalHandler-->>Client: 404 Not Found<br/>{status:404, error:"Not Found",<br/>message:"User not found: {id}"}
    end
```

## Swagger UI Request (Cloud Run)

```mermaid
sequenceDiagram
    participant Browser
    participant SwaggerUI as Swagger UI
    participant CloudRun as Cloud Run Proxy
    participant App as Spring Boot

    Browser->>SwaggerUI: Load /swagger-ui.html
    SwaggerUI->>CloudRun: GET /api/v1/v3/api-docs
    CloudRun->>App: Forward (X-Forwarded-Proto: https)
    App-->>SwaggerUI: OpenAPI JSON spec
    SwaggerUI->>Browser: Render API documentation

    Note over Browser,SwaggerUI: User clicks "Try it out"
    Browser->>CloudRun: POST /api/v1/users<br/>Authorization: Basic xxx
    CloudRun->>App: Forward request
    App-->>CloudRun: 201 Created
    CloudRun-->>Browser: Response
```
