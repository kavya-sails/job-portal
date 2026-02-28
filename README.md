# Job Portal Backend System

## Project overview

This repository contains backend microservices for a Job Portal application built with Spring Boot. The primary focus is on secure user management, job listings, and job applications. The codebase illustrates a pragmatic microservices design (API Gateway + Eureka + user-service + job-service) and demonstrates: REST APIs, JWT-based authentication, Spring Data JPA, CQRS (applied to job-service), validation, exception handling, and comprehensive tests.

---

# Architecture

* **api-gateway** (Spring Cloud Gateway, WebFlux)

    * Responsible for routing, JWT validation (edge auth), and propagating authenticated user headers (`X-User-Id`, `X-User-Username`, `X-User-Role`).
    * Stateless — no DB.

* **eureka-server** (service registry; optional for simple demos)

* **user-service** (Spring Boot, MVC)

    * Handles registration, login, profile management, and credential store.
    * Stores users and profiles (single DB schema is fine).
    * Issues JWTs on successful login.

* **job-service** (Spring Boot, MVC)

    * Manages job listings and job applications.
    * Uses CQRS pattern internally: `JobCommandController` (writes) and `JobQueryController` (reads).
    * Applications and jobs are persisted using Spring Data JPA.

* **Shared practices**

    * `@ControllerAdvice` centralizes exception handling.
    * DTOs/Mapper layer isolates entities from API.
    * Postgres (or H2 for demo) + Flyway for migrations.

---

# Tech stack

* Java 17+
* Spring Boot 3.x
* Spring Security (JWT)
* Spring Data JPA, Hibernate
* Spring Cloud Gateway (WebFlux)
* Flyway (DB migrations)
* PostgreSQL (prod) / H2 (dev/demo)
* JUnit 5, Mockito, Testcontainers for tests
* Swagger for API Documentation

---

# How JWT is handled (short)

* **Login** (`user-service`): user authenticates with email/password → server returns a signed JWT containing `sub` (user id), `username` (email), `roles`, and other claims.
* **Gateway**: validates JWT signature (using same secret/public key) by decoding with a `JwtDecoder`. If valid, gateway converts token → `Authentication` and populates Reactor Security Context. It also propagates `X-User-Id` and other headers downstream.
* **Services**: may either trust headers from gateway or independently validate JWT. For simplicity,gateway propagates headers and uses them in controllers.

---

# API overview (important endpoints)

> All endpoints under `/api` prefix. Replace `http://localhost:{port}` accordingly.

## User Service

* `POST /api/auth/register` — register new user

    * Body: `{ "email": "kavya@gmail.com", "password": "P@ssw0rd", "roleName": "USER" }`
* `POST /api/auth/login` — login and receive JWT

    * Body: `{ "email": "kavya@gmail.com", "password": "P@ssw0rd" }`
    * Response: `{ "accessToken": "Bearer ...", "tokenType": "Bearer" }`
* `GET /api/users/{id}` — fetch user profile (secured)
* `PUT /api/users/{id}` — update profile (secured)

## Job Service

* `GET /api/jobs?title=java&location=bengaluru&page=0&size=10` — search with pagination
* `POST /api/jobs` — create job (secured, ROLE_ADMIN/ROLE_RECRUITER)
* `GET /api/jobs/{id}` — job details (public)
* `PUT /api/jobs/{id}` — update job
* `DELETE /api/jobs/{id}` — delete job

## Applications

* `POST /api/jobs/applications/{jobId}` — apply for a job (secured)

    * Requires header `X-User-Id` (populated by gateway) or `Authorization: Bearer <token>` if your service validates JWT
* `PUT /api/jobs/applications/{applicationId}/status` — update status (ADMIN)
* `GET /api/jobs/applications/history/{userId}` — application history (owner or ADMIN)

---

# Email Notifications using RabbitMQ

The system includes an asynchronous notification mechanism using **RabbitMQ**:

### **Flow**
1. When a recruiter updates a job application's status (e.g., from *APPLIED* to *REVIEWING*),  
   the **job-service publishes a message** to a RabbitMQ exchange.
2. **Notification Consumer** listens to the queue.
3. The consumer retrieves the message and **sends an email to the user** with the new application status.

### **Purpose**
- Avoids blocking the main API thread.
- Ensures reliable background email delivery.
- Allows scaling the email service independently.
