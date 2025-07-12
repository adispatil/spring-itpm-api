# API Logging System

This Spring Boot application includes a comprehensive API logging system that automatically captures and stores detailed information about all API calls made to the application.

## Features

### Automatic Logging
- **Automatic Capture**: All API calls to `/api/**` endpoints are automatically logged
- **Asynchronous Logging**: Logs are saved asynchronously to avoid impacting API response times
- **Comprehensive Data**: Captures request/response details, timing, user info, and more

### Logged Information
- **Endpoint**: The API endpoint that was called
- **Method**: HTTP method (GET, POST, PUT, DELETE, etc.)
- **User ID**: User making the request (extracted from headers)
- **Request Body**: Complete request payload
- **Response Status**: HTTP status code
- **Response Body**: Complete response payload
- **User Agent**: Browser/client information
- **IP Address**: Client IP address (with proxy support)
- **Execution Time**: Request processing time in milliseconds
- **Timestamp**: Exact date and time of the request
- **Error Messages**: Any exceptions that occurred
- **Request Headers**: All request headers
- **Query Parameters**: URL query parameters
- **Path Parameters**: URL path parameters

## Database Schema

The logs are stored in MongoDB in the `api_logs` collection with the following structure:

```json
{
  "_id": "ObjectId",
  "endpoint": "/api/users/register",
  "method": "POST",
  "userId": "user123",
  "requestBody": "{\"userid\":\"newuser\",\"password\":\"***\",\"name\":\"New User\",\"email\":\"newuser@example.com\"}",
  "responseStatus": 201,
  "responseBody": "{\"success\":true,\"message\":\"User registered successfully\"}",
  "userAgent": "Mozilla/5.0...",
  "ipAddress": "192.168.1.100",
  "executionTime": 150,
  "timestamp": "2024-01-15T10:30:45.123",
  "errorMessage": null,
  "requestHeaders": {
    "Content-Type": "application/json",
    "Accept": "application/json"
  },
  "queryParams": {},
  "pathParams": {}
}
```

## API Endpoints for Viewing Logs

### Get All Logs
```
GET /api/logs
```

**Query Parameters:**
- `userId` (optional): Filter by user ID
- `endpoint` (optional): Filter by endpoint
- `method` (optional): Filter by HTTP method
- `status` (optional): Filter by response status
- `startDate` (optional): Filter by start date (ISO format)
- `endDate` (optional): Filter by end date (ISO format)
- `limit` (optional, default: 100): Limit number of results

**Example:**
```
GET /api/logs?userId=user123&method=POST&limit=50
```

### Manual Cleanup
```
POST /api/logs/cleanup/manual
```

**Response:**
```json
{
  "success": true,
  "message": "Manual cleanup completed",
  "deletedCount": 150
}
```

### Get Cleanup Statistics
```
GET /api/logs/cleanup/stats
```

**Response:**
```json
{
  "retentionDays": 7,
  "cutoffDate": "2024-01-08T02:00:00",
  "logsToDelete": 150,
  "cleanupEnabled": true,
  "batchSize": 1000
}
```

### Get Error Logs
```
GET /api/logs/errors
```

**Query Parameters:**
- `userId` (optional): Filter by user ID

### Get User Logs
```
GET /api/logs/users/{userId}
```

### Get Endpoint Logs
```
GET /api/logs/endpoints/{endpoint}
```

### Get Method Logs
```
GET /api/logs/methods/{method}
```

### Get Status Logs
```
GET /api/logs/status/{status}
```

### Get Logs by Date Range
```
GET /api/logs/date-range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
```

**Query Parameters:**
- `startDate` (required): Start date in ISO format
- `endDate` (required): End date in ISO format
- `method` (optional): Filter by HTTP method
- `endpoint` (optional): Filter by endpoint

### Get Log Statistics
```
GET /api/logs/stats
```

**Query Parameters:**
- `startDate` (optional): Start date for statistics
- `endDate` (optional): End date for statistics

**Response:**
```json
{
  "totalRequests": 1250,
  "successfulRequests": 1180,
  "errorRequests": 70,
  "averageResponseTime": 145.5,
  "uniqueUsers": 45,
  "uniqueEndpoints": 8,
  "methods": {
    "GET": 800,
    "POST": 300,
    "PUT": 100,
    "DELETE": 50
  },
  "statusCodes": {
    "200": 750,
    "201": 300,
    "400": 30,
    "404": 20,
    "500": 20
  }
}
```

## Configuration

### Interceptor Configuration
The logging interceptor is automatically configured to:
- Apply to all `/api/**` endpoints
- Exclude `/api/logs/**` endpoints to prevent infinite loops
- Use content caching for request/response body capture

### Content Caching
The system uses Spring's `ContentCachingRequestWrapper` and `ContentCachingResponseWrapper` to capture request and response bodies without interfering with the normal request flow.

### Log Cleanup Configuration
The automatic cleanup system is configured with the following properties in `application.properties`:

```properties
# Log Cleanup Configuration
app.log-cleanup.retention-days=7
app.log-cleanup.batch-size=1000
app.log-cleanup.enabled=true
app.log-cleanup.cron-expression=0 2 * * *
```

**Configuration Options:**
- `retention-days`: Number of days to keep logs (default: 7)
- `batch-size`: Number of logs to delete in each batch (default: 1000)
- `enabled`: Enable/disable automatic cleanup (default: true)
- `cron-expression`: Schedule for cleanup (default: daily at 2 AM)

## Usage Examples

### 1. View Recent API Calls
```bash
curl "http://localhost:8081/api/logs?limit=10"
```

### 2. View User's API Activity
```bash
curl "http://localhost:8081/api/logs/users/user123"
```

### 3. View Error Logs
```bash
curl "http://localhost:8081/api/logs/errors"
```

### 4. View API Statistics
```bash
curl "http://localhost:8081/api/logs/stats"
```

### 5. View Logs for Specific Date Range
```bash
curl "http://localhost:8081/api/logs/date-range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59"
```

### 6. View POST Requests Only
```bash
curl "http://localhost:8081/api/logs/methods/POST"
```

### 7. Trigger Manual Cleanup
```bash
curl -X POST "http://localhost:8081/api/logs/cleanup/manual"
```

### 8. Get Cleanup Statistics
```bash
curl "http://localhost:8081/api/logs/cleanup/stats"
```

## Performance Considerations

1. **Asynchronous Logging**: Logs are saved asynchronously to avoid blocking API responses
2. **Error Handling**: Logging errors don't affect the main API functionality
3. **Content Caching**: Uses efficient content caching for request/response body capture
4. **Automatic Cleanup**: Logs are automatically deleted after 7 days to prevent database bloat
5. **Batch Processing**: Cleanup uses batch processing to handle large datasets efficiently
6. **MongoDB Indexing**: Consider adding indexes on frequently queried fields:
   - `userId`
   - `timestamp`
   - `endpoint`
   - `method`
   - `responseStatus`

## Security Considerations

1. **Sensitive Data**: Consider what data should be logged vs. masked
2. **Data Retention**: Implement log rotation and cleanup policies
3. **Access Control**: The log viewing endpoints should be protected with appropriate authentication
4. **GDPR Compliance**: Ensure logged data complies with privacy regulations

## Monitoring and Alerts

You can use the logging system to:
- Monitor API usage patterns
- Track error rates and types
- Identify slow endpoints
- Monitor user activity
- Set up alerts for unusual patterns

## Troubleshooting

### Logs Not Being Captured
1. Check that the interceptor is properly configured
2. Verify the endpoint matches `/api/**` pattern
3. Check MongoDB connection
4. Review application logs for interceptor errors

### Performance Issues
1. Check MongoDB performance
2. Consider adding database indexes
3. Review log query patterns
4. Implement log rotation if needed

### Missing Request/Response Bodies
1. Verify content caching filter is active
2. Check request/response content type
3. Ensure proper content length headers 