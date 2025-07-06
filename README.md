# Spring Boot User Registration API

A Spring Boot application with MongoDB integration for user registration and management.

## Features

- User registration with MongoDB storage
- User validation/authentication
- Unique userid and email validation
- RESTful API endpoints
- Comprehensive test coverage

## API Endpoints

### Register New User
```
POST /api/users/register
Content-Type: application/json

{
    "userid": "newuser",
    "password": "password123",
    "name": "New User",
    "email": "newuser@example.com"
}
```

**Response (Success - 201 Created):**
```json
{
    "success": true,
    "message": "User registered successfully",
    "user": {
        "id": "507f1f77bcf86cd799439011",
        "userid": "newuser",
        "password": "***",
        "name": "New User",
        "email": "newuser@example.com",
        "isActive": true
    }
}
```

**Response (Error - 400 Bad Request):**
```json
{
    "success": false,
    "message": "User with userid 'newuser' already exists"
}
```

### Validate User
```
POST /api/users/validate
Content-Type: application/json

{
    "userid": "admin",
    "password": "admin123"
}
```

### Get All Users
```
GET /api/users
```

### Get User by User ID
```
GET /api/users/{userid}
```

## Configuration

The application is configured to connect to MongoDB Atlas. Configuration is in `application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/?retryWrites=true&w=majority
spring.data.mongodb.database=ITPM_Server
server.port=8081
```

## Running the Application

1. Ensure MongoDB is running and accessible
2. Update the MongoDB connection string in `application.properties` if needed
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

## Testing

Run the tests:
```bash
./gradlew test
```

The application includes:
- Unit tests for service layer
- Integration tests for controller layer
- MongoDB repository tests using Testcontainers

## Database Schema

The `User` collection in MongoDB has the following structure:

```json
{
    "_id": "ObjectId",
    "userid": "String (unique)",
    "password": "String",
    "name": "String",
    "email": "String (unique)",
    "isActive": "Boolean"
}
```

## Security Notes

- Passwords are stored in plain text (consider hashing for production)
- Passwords are masked in API responses
- Unique constraints on userid and email prevent duplicates 