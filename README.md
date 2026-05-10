# AI Policy Drafting Assistant – Backend

## Overview
The AI Policy Drafting Assistant is a backend application developed using Spring Boot to help users create, manage, and analyze policies efficiently using Artificial Intelligence. The system provides secure authentication, policy management, file handling, AI-powered report generation, and Swagger API documentation.

---

# Features

- User Authentication using JWT
- Policy Creation and Management
- File Upload and Download
- AI-based Policy Analysis
- Swagger/OpenAPI Documentation
- Validation and Exception Handling
- Redis Caching Support
- MySQL Database Integration
- Docker Support
- Unit Testing with JUnit and Mockito

---

# Technologies Used

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- MySQL
- Redis
- Swagger / OpenAPI
- Maven
- Docker
- JUnit & Mockito

---

# Project Structure

```text
src
 ├── main
 │   ├── java
 │   │   └── com.internship.tool
 │   │       ├── controller
 │   │       ├── service
 │   │       ├── repository
 │   │       ├── entity
 │   │       ├── config
 │   │       ├── exception
 │   │       └── dto
 │   └── resources
 │       └── application.yml
 └── test
     └── java
```

---

# API Modules

## Auth Controller
- User Registration
- User Login
- JWT Token Generation

## Policy Controller
- Create Policy
- Get All Policies
- Get Policy By ID
- Delete Policy

## File Controller
- Upload Files
- Download Files

---

# Swagger API Documentation

After running the application, Swagger UI can be accessed at:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# How to Run the Project

## Clone Repository

```bash
git clone <repository-url>
```

---

## Navigate to Backend Folder

```bash
cd backend
```

---

## Run Application

```bash
.\mvnw spring-boot:run
```

---

# Database Configuration

Update database details in:

```text
src/main/resources/application.yml
```

Example:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tooldb
    username: root
    password: your_password
```

---

# File Upload Support

The application supports multipart file upload using Swagger and Spring Boot MultipartFile handling.

---

# Testing

Run tests using:

```bash
.\mvnw test
```

Skip tests:

```bash
.\mvnw clean install -DskipTests
```

---

# Docker Support

Run application using Docker:

```bash
docker-compose up --build
```

---

# Future Enhancements

- Role-Based Access Control
- Advanced AI Recommendations
- Cloud Deployment
- Notification System
- Policy Version Tracking

---

# Conclusion

The AI Policy Drafting Assistant backend simplifies policy management using AI-powered automation, secure authentication, file processing, and scalable REST APIs. The system is designed to improve efficiency, reduce manual effort, and provide better policy management capabilities.
