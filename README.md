# Authentication API

A secure RESTful Authentication API built using Spring Boot, Spring Security, JWT, BCrypt, MySQL, and JUnit.

The project provides user registration, login, JWT-based authentication, role-based authorization, exception handling, Swagger documentation, automated tests, and JaCoCo code coverage.

---

## Features

- User registration
- Email validation
- Password validation
- BCrypt password encryption
- User login
- JWT token generation
- JWT-based authentication
- Protected REST APIs
- Role-based authorization
- USER and ADMIN roles
- Custom 401 and 403 error responses
- Global exception handling
- Swagger/OpenAPI documentation
- Unit testing
- Controller testing
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
| BCrypt | Password encryption |
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