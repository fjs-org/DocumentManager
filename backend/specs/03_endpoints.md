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

### Get User
- **Method:** `GET`
- **Path:** `/api/v1/users/{id}`
- **Response:** HTTP 200 with User object JSON if found, else HTTP 404 Not Found.

## Document Endpoints

### Create Document
- **Method:** `POST`
- **Path:** `/api/v1/documents`
- **Request Body:**
  - Mandatory: Multipart/form-data: Contains the `file`
  - Document Metadata:
    - `fileName`: String (Mandatory, original file name of the uploaded document)
- **Business Logic:**
    - Validate the file size:
        - If file size > 1 MB, throw a custom `FileSizeExceededException` mapped to HTTP 400.
    - Set ownerId to null
    - Persist file and metadata into the database.
- **Response:** 
  - HTTP 200 with Document object JSON if saved successfully, else HTTP 400 Bad Request.
  - RULE: Do not place binary file in JSON response

### Get Documents
- **Method:** `GET`
- **Path:** `/api/v1/documents`
- **Response:** HTTP 200 with List of all documents.
- **Business Logic:**
    - Return only metadata of documents (id, fileName, createdAt) without binary content.

### Get Document
- **Method:** `GET`
- *Path:* `/api/v1/documents/{id}`
- **Response:** HTTP 200 with Document object JSON if found, else HTTP 404 Not Found.
- **Business Logic:**
  - Return metadata of the document (id, fileName, createdAt) including binary content (data).