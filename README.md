# Permission Service 2025

## Description
Service for managing code snippet access permissions developed with Spring Boot and Kotlin. Allows controlling who can read, write, or own each snippet.

## Features

- **Permission Management**: Create, update, and delete permissions for snippets
- **Permission Verification**: Check if a user has access to a snippet
- **Write Permission Check**: Verify write permissions (OWNER or WRITE)
- **Snippet Queries**: Get all permissions for a snippet
- **User Queries**: Get all permissions for a user

## Permission Roles

- **OWNER**: Owner of the snippet, has all permissions
- **WRITE**: Can read and modify the snippet
- **READ**: Can only read the snippet

## Running the Project

### With Docker
```bash
docker-compose up -d --build
```

### Local Development
```bash
./gradlew bootRun
```

### Access Swagger
Once running: http://localhost:8081/swagger-ui.html

## Main Endpoints

- `POST /api/permissions` - Create a new permission
- `GET /api/permissions/check` - Check user permission
- `GET /api/permissions/write-check` - Check write permission
- `GET /api/permissions/snippet/{snippetId}` - Get permissions for a snippet
- `GET /api/permissions/user/{userId}` - Get permissions for a user
- `PUT /api/permissions/{snippetId}/{userId}` - Update permission
- `DELETE /api/permissions/{snippetId}/{userId}` - Delete permission

## Technologies

- **Spring Boot 3.5.6** - Framework
- **Kotlin 1.9.25** - Language
- **Spring Data JPA** - Persistence
- **PostgreSQL** - Database
- **Swagger/OpenAPI** - Documentation
- **ktlint** - Automatic linting

## Configuration

The service runs on port **8081** by default.

Environment variables (configured in `docker-compose.yml`):
- `DB_HOST`: PostgreSQL host
- `DB_PORT`: PostgreSQL port
- `DB_NAME`: Database name
- `DB_USER`: PostgreSQL user
- `DB_PASSWORD`: PostgreSQL password

## Integration

This service is used by:
- **Snippet Service**: Checks permissions before allowing snippet operations
- **Asset Service**: May check permissions for asset access

## Docker Build

The Dockerfile uses a multi-stage build:
1. **Builder stage**: Builds the JAR using Gradle
2. **Runtime stage**: Creates a lightweight runtime image with the JAR

## Development Notes

- Uses JWT authentication via Auth0
- CORS is configured for cross-origin requests
- Includes comprehensive error handling
- Supports ktlint for code quality

