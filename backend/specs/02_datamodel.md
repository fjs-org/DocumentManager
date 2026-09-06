# Data Model Specification

## Entities

### 1. Document
- `id`: UUID (Primary Key, Auto-generated)
- `fileName`: String (Optional, original file name of the uploaded document)
- `data`: Binary Document (Not Null, Max size 1 MB)
- `createdAt`: LocalDateTime (Auto-populated on creation)
- `ownerId`: UUID (Optional, Foreign Key linking to User.id)

### 2. User
- `id`: UUID (Primary Key, Auto-generated)
- `email`: String (Unique, Not Null, Email Validation)
- `fullName`: String (Not Null, Max 100 chars)
- `createdAt`: LocalDateTime (Auto-populated on creation)

## Relationships
- **User to Document:** One-to-Many. A user can own multiple documents. If a user is deleted, cascade delete their documents.