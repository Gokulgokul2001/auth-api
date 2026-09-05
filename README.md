# Authentication API

A secure RESTful Authentication API built using Spring Boot, Spring Security, JWT, BCrypt, MySQL, and JUnit.

The project provides user registration, login, JWT-based authentication, role-based authorization, password reset functionality, exception handling, Swagger documentation, automated tests, and JaCoCo code coverage.

---

## Features

- User registration
- Email validation
- Password validation
- BCrypt password hashing
- User login
- JWT token generation
- JWT-based authentication
- Protected REST APIs
- Role-based authorization
- USER and ADMIN roles
- User management for administrators
- Update user details
- Delete users
- Forgot password
- Secure password reset token generation
- Password reset token expiration
- Password reset
- Custom 401 and 403 error responses
- Global exception handling
- Swagger/OpenAPI documentation
- Unit testing
- Controller testing
- Security testing
- JaCoCo code coverage
- MySQL database integration

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Web MVC | REST APIs |
| Spring Data JPA | Database access |
| Spring Security | Authentication and authorization |
| JWT | Token-based authentication |
| BCrypt | Password hashing |
| MySQL 8 | Database |
| Maven | Build and dependency management |
| JUnit 5 | Testing |
| Mockito | Mocking |
| MockMvc | Controller testing |
| JaCoCo | Code coverage |
| Swagger / OpenAPI | API documentation |
| Git | Version control |
| GitHub | Source code hosting |

---

## Project Architecture

The application follows a layered architecture:

```text
Client
   |
   v
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
MySQL Database
```

### Complete System Architecture

```text
                         ┌──────────────────────┐
                         │     React Frontend   │
                         │        (Vite)        │
                         │                      │
                         │ Login / Register     │
                         │ User Dashboard       │
                         │ Admin Dashboard      │
                         │ Password Reset       │
                         └──────────┬───────────┘
                                    │
                              REST API / JSON
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   Spring Boot API    │
                         │                      │
                         │ Controllers          │
                         │ Services             │
                         │ Repositories         │
                         └──────────┬───────────┘
                                    │
                         ┌──────────▼───────────┐
                         │   Spring Security    │
                         │                      │
                         │ JWT Filter           │
                         │ JWT Validation       │
                         │ Role Authorization   │
                         │ BCrypt               │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │        MySQL         │
                         │                      │
                         │      auth_db         │
                         │        users         │
                         └──────────────────────┘
```

### Authentication Flow

```text
React Login
     |
     v
POST /api/auth/login
     |
     v
AuthController
     |
     v
AuthService
     |
     ├── Find User ───────► MySQL
     |
     ├── Verify BCrypt Password
     |
     └── Generate JWT
              |
              v
        React LocalStorage
              |
              v
   Authorization: Bearer JWT
              |
              v
   JwtAuthenticationFilter
              |
              v
       Spring Security
              |
       ┌──────┴──────┐
       v             v
     USER          ADMIN
       |             |
       v             v
 /api/user      /api/admin/**
```

### Password Reset Flow

```text
Forgot Password
       |
       v
POST /api/auth/forgot-password
       |
       v
Generate Secure Random Token
       |
       v
Store Token + Expiry
       |
       v
Reset Password
       |
       v
POST /api/auth/reset-password
       |
       v
Validate Token + Expiry
       |
       v
Hash New Password
       |
       v
Clear Reset Token
       |
       v
Password Updated
       |
       v
Login with New Password
```

---

## Project Structure

```text
src
└── main
    └── java
        └── com.gokul.auth_api
            │
            ├── AuthApiApplication.java
            │
            ├── config
            │   ├── OpenApiConfig.java
            │   └── SecurityConfig.java
            │
            ├── controller
            │   ├── AuthController.java
            │   ├── UserController.java
            │   └── AdminController.java
            │
            ├── dto
            │   ├── RegisterRequest.java
            │   ├── LoginRequest.java
            │   ├── RegisterResponse.java
            │   ├── LoginResponse.java
            │   ├── ErrorResponse.java
            │   ├── ValidationErrorResponse.java
            │   ├── UserResponse.java
            │   ├── UpdateUserRequest.java
            │   ├── ForgotPasswordRequest.java
            │   └── ResetPasswordRequest.java
            │
            ├── exception
            │   ├── EmailAlreadyExistsException.java
            │   ├── InvalidCredentialsException.java
            │   ├── InvalidResetTokenException.java
            │   ├── JwtAccessDeniedHandler.java
            │   ├── JwtAuthenticationEntryPoint.java
            │   └── GlobalExceptionHandler.java
            │
            ├── model
            │   └── User.java
            │
            ├── repository
            │   └── UserRepository.java
            │
            ├── security
            │   ├── JwtService.java
            │   └── JwtAuthenticationFilter.java
            │
            └── service
                ├── AuthService.java
                └── CustomUserDetailsService.java
```

---

## Authentication

### Registration

A new user can register using:

```text
POST /api/auth/register
```

The registration process:

1. Validates the request
2. Checks whether the email already exists
3. Hashes the password using BCrypt
4. Assigns the `USER` role
5. Saves the user in MySQL
6. Returns the registration response

### Login

A registered user can log in using:

```text
POST /api/auth/login
```

The login process:

1. Finds the user using the email
2. Verifies the password using BCrypt
3. Generates a JWT
4. Returns the JWT and user information

---

## JWT Authentication

The application uses JSON Web Tokens for authentication.

After successful login, the backend returns a JWT token.

The frontend stores the token and sends it with protected requests using:

```text
Authorization: Bearer <JWT>
```

The `JwtAuthenticationFilter`:

1. Reads the Authorization header
2. Extracts the JWT
3. Validates the JWT signature
4. Checks token expiration
5. Extracts the user's email
6. Loads the user
7. Creates the Spring Security authentication
8. Stores the authentication in the SecurityContext

---

## Role-Based Authorization

The application supports two roles:

### USER

Users can access:

```text
GET /api/user
```

### ADMIN

Administrators can access:

```text
GET /api/user
GET /api/admin/**
```

Admin functionality includes:

- View users
- Update users
- Delete users
- Manage user roles

Spring Security is responsible for enforcing these permissions.

---

## Password Reset

The application provides a password reset mechanism using a secure temporary token.

### Forgot Password

```text
POST /api/auth/forgot-password
```

The backend:

1. Finds the user
2. Generates a cryptographically secure random token
3. Stores the token
4. Stores the token expiration time
5. Returns the reset token for development/testing

The reset token expires after **15 minutes**.

### Reset Password

```text
POST /api/auth/reset-password
```

The backend:

1. Finds the user using the reset token
2. Validates the token
3. Checks token expiration
4. Hashes the new password using BCrypt
5. Updates the password
6. Clears the reset token
7. Clears the token expiration time

After a successful reset, the user can log in using the new password.

---

## API Endpoints

### Authentication APIs

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Authenticate user and generate JWT |
| POST | `/api/auth/forgot-password` | Public | Generate password reset token |
| POST | `/api/auth/reset-password` | Public | Reset password using token |

### User APIs

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/user` | USER / ADMIN | Access user information |

### Admin APIs

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/admin/users` | ADMIN | Retrieve users |
| PUT | `/api/admin/users/{id}` | ADMIN | Update user |
| DELETE | `/api/admin/users/{id}` | ADMIN | Delete user |

---

## Database

The application uses MySQL.

Database:

```text
auth_db
```

Main table:

```text
users
```

### Users Table

| Column | Purpose |
|---|---|
| id | Unique user identifier |
| username | User name |
| email | User email address |
| password | BCrypt password hash |
| role | USER or ADMIN |
| reset_token | Temporary password reset token |
| reset_token_expiry | Reset token expiration time |

Passwords are never stored as plain text.

---

## Validation

The application uses Jakarta Bean Validation.

Examples include:

```text
Username is required
Email is required
Invalid email format
Password is required
Password must be at least 6 characters
```

Validation errors are returned using a structured response.

Example:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "Invalid email format"
  }
}
```

---

## Exception Handling

The application uses centralized exception handling through:

```text
GlobalExceptionHandler
```

Handled exceptions include:

- Duplicate email
- Invalid credentials
- Invalid reset token
- Expired reset token
- Validation errors
- Authentication errors
- Authorization errors

### Unauthorized Response

```json
{
  "status": 401,
  "message": "Authentication required"
}
```

### Forbidden Response

```json
{
  "status": 403,
  "message": "Access denied"
}
```

### Invalid Credentials

```json
{
  "status": 401,
  "message": "Invalid email or password"
}
```

---

## Swagger / OpenAPI

Swagger/OpenAPI is integrated into the application for API documentation and testing.

Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8081/v3/api-docs
```

---

## CORS Configuration

The backend allows requests from the React development servers:

```text
http://localhost:5173
http://localhost:5174
```

Allowed HTTP methods include:

```text
GET
POST
PUT
DELETE
OPTIONS
```

---

## Configuration

Sensitive configuration such as database credentials and JWT secrets should not be committed to GitHub.

Example configuration:

```properties
server.port=8081

spring.datasource.url=jdbc:mysql://localhost:3306/auth_db
spring.datasource.username=root
spring.datasource.password=<your-password>

spring.jpa.hibernate.ddl-auto=update

jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
```

The JWT secret should be provided through an environment variable.

---

## Testing

The project includes automated tests covering:

- Registration
- Login
- Password validation
- Duplicate email handling
- Password reset
- Invalid reset token
- Expired reset token
- JWT generation
- JWT validation
- JWT authentication filter
- Controller endpoints
- Security behavior

Run the complete test suite:

```bash
./mvnw clean test
```

Expected result:

```text
BUILD SUCCESS
```

---

## Code Coverage

JaCoCo is used to measure code coverage.

Current coverage:

```text
Instruction Coverage: approximately 84%
Branch Coverage: 100%
```

Generate the coverage report using:

```bash
./mvnw clean test
```

The report is available under:

```text
target/site/jacoco/
```

---

## Running the Application

### Prerequisites

Install:

- Java 17
- MySQL 8
- Maven
- Git

### Clone the Repository

```bash
git clone https://github.com/Gokulgokul2001/auth-api.git
```

Navigate to the project:

```bash
cd auth-api
```

### Create Database

Create the MySQL database:

```sql
CREATE DATABASE auth_db;
```

### Configure Environment

Set the JWT secret environment variable:

```text
JWT_SECRET
```

Configure your local database credentials in your local application configuration.

Do not commit sensitive credentials to GitHub.

### Start the Application

```bash
./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8081
```

---

## Build the Application

To create a production build:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw clean test
```

---

## Security Practices

The application implements the following security practices:

- BCrypt password hashing
- JWT-based authentication
- JWT expiration
- Secure random password reset tokens
- Password reset token expiration
- Reset token cleanup after successful password reset
- Role-based authorization
- Protected REST APIs
- Generic invalid-login message
- Database credentials excluded from Git
- JWT secrets excluded from Git
- Custom authentication and authorization error handling

---

## Frontend

A React frontend is available for this authentication API.

The frontend provides:

- Login
- Registration
- User dashboard
- Admin dashboard
- User management
- Forgot password
- Reset password
- Logout
- JWT session handling

Frontend repository:

```text
https://github.com/Gokulgokul2001/auth-frontend
```

---

## Project Repositories

### Backend

```text
https://github.com/Gokulgokul2001/auth-api
```

### Frontend

```text
https://github.com/Gokulgokul2001/auth-frontend
```

---

## Future Improvements

Potential future improvements include:

- Email-based password reset
- Generic forgot-password response
- Refresh token support
- Token revocation
- Account lockout after repeated failed logins
- Password strength requirements
- Rate limiting
- Stateless session configuration
- Docker support
- CI/CD pipeline
- HTTPS deployment
- Cloud deployment
- Centralized logging and monitoring
- Automated frontend testing

---

## Author

**Gokul**

---

## License

This project is created for learning, development, and portfolio purposes.