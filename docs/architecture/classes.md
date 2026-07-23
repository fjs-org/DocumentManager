# Domain Model & Class Diagrams

## Entity Relationships

```mermaid
classDiagram
    class User {
        <<JPA Entity>>
        +UUID id
        +String email
        +String fullName
        +LocalDateTime createdAt
        +List~Document~ documents
        +onCreate() void
    }

    class Document {
        <<JPA Entity>>
        +UUID id
        +String location
        +byte[] data
        +LocalDateTime createdAt
        +User owner
        +onCreate() void
    }

    class UserDto {
        <<DTO>>
        +UUID id
        +String email
        +String fullName
        +LocalDateTime createdAt
    }

    class ErrorResponse {
        <<DTO>>
        +LocalDateTime timestamp
        +int status
        +String error
        +String message
        +String path
    }

    class UserService {
        <<Interface>>
        +createUser(UserDto) UserDto
        +getAllUsers() List~UserDto~
        +getUserById(UUID) UserDto
    }

    class UserServiceImpl {
        <<Service>>
        -UserRepository userRepository
        -UserMapper userMapper
        +createUser(UserDto) UserDto
        +getAllUsers() List~UserDto~
        +getUserById(UUID) UserDto
    }

    class UserRepository {
        <<Interface>>
        +existsByEmail(String) boolean
    }

    class DocumentRepository {
        <<Interface>>
    }

    class UserMapper {
        <<MapStruct Interface>>
        +toEntity(UserDto) User
        +toDto(User) UserDto
    }

    class UserController {
        <<REST Controller>>
        -UserService userService
        +getAllUsers() ResponseEntity~List~UserDto~~
        +getUserById(UUID) ResponseEntity~UserDto~
        +createUser(UserDto) ResponseEntity~UserDto~
    }

    class GlobalExceptionHandler {
        <<RestControllerAdvice>>
        +handleUserNotFound(UserNotFoundException, HttpServletRequest) ResponseEntity~ErrorResponse~
        +handleEmailAlreadyExists(EmailAlreadyExistsException, HttpServletRequest) ResponseEntity~ErrorResponse~
        +handleValidation(MethodArgumentNotValidException, HttpServletRequest) ResponseEntity~ErrorResponse~
    }

    User "1" --> "*" Document : owns
    Document --> User : owner

    UserService <|.. UserServiceImpl : implements
    UserRepository <|-- UserRepository : extends JpaRepository
    DocumentRepository <|-- DocumentRepository : extends JpaRepository
    UserMapper ..> UserDto : maps to/from
    UserMapper ..> User : maps to/from

    UserController --> UserService : uses
    UserServiceImpl --> UserRepository : uses
    UserServiceImpl --> UserMapper : uses
    GlobalExceptionHandler ..> ErrorResponse : produces
```

## DTO Mapping Rules

```mermaid
flowchart LR
    subgraph "UserDto"
        direction LR
        UId[id - READ_ONLY]
        UEmail[email - @Email @NotBlank]
        UName[fullName - @NotBlank @Size max=100]
        UCreated[createdAt - READ_ONLY]
    end

    subgraph "User Entity"
        direction LR
        EId[id - Generated UUID]
        EEmail[email - unique not null]
        EName[fullName - not null max=100]
        ECreated[createdAt - PrePersist]
        EDocs[documents - OneToMany]
    end

    UId -->|@Mapping ignore=true| EId
    UEmail --> EEmail
    UName --> EName
    UCreated -->|@Mapping ignore=true| ECreated
    EDocs -->|@Mapping ignore=true| UDocs

    style UId fill:#ff9,stroke:#333
    style UCreated fill:#ff9,stroke:#333
```

## Exception Handling Chain

```mermaid
flowchart TD
    A[Exception Thrown] --> B{Exception Type}

    B -->|UserNotFoundException| C[GlobalExceptionHandler]
    B -->|EmailAlreadyExistsException| C
    B -->|MethodArgumentNotValidException| C
    B -->|Unauthenticated Request| D[AuthenticationEntryPoint]

    C --> E[ErrorResponse JSON]
    D --> E

    E --> F[timestamp]
    E --> G[status: 404/400/403]
    E --> H[error: Not Found / Bad Request / Forbidden]
    E --> I[message: human-readable detail]
    E --> J[path: request URI]

    style E fill:#f9f,stroke:#333
```
