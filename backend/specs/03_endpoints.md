# REST API Endpoint Specification

## User Endpoints

### Create User
- **Method:** `POST`
- **Path:** `/api/v1/users`
- **Request Body:** JSON matching User details (excluding id, createdAt)
- **Business Logic:**
    - Check if email already exists. If yes, throw a custom `EmailAlreadyExistsException` mapped to HTTP 400.
- **Response:** HTTP 201 Created with the saved User object JSON.

### Get Users
- **Method:** `GET`
- **Path:** `/api/v1/users`
- **Response:** HTTP 200 with List of all users.

