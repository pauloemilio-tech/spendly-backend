Spendly 💰 — Backend

Spendly is a backend system for a personal finance management platform under active development, designed with a focus on security, scalability, clean architecture and real-world financial workflows.

The project is being built as a realistic backend application, emphasizing authentication, ownership validation, domain organization and maintainable software engineering practices.

🚧 Project Status: Active Development

🎯 Overview

Spendly aims to simulate the backend architecture of a modern financial platform, focusing on:

Secure authentication and authorization
User-based resource ownership
Scalable and maintainable architecture
Clean separation of concerns
Business validation and domain consistency
Real-world API design practices
🚀 Tech Stack
Java 17+
Spring Boot
Spring Web
Spring Data JPA
Spring Security
Bean Validation
JWT Authentication
PostgreSQL
Hibernate / JPA
Maven
Lombok
Docker
Docker Compose
🧱 Current Stage

The backend currently includes:

Full JWT authentication flow
Protected routes with Spring Security
Customer registration and login
Wallet management module
Ownership validation for authenticated users
DTO-based request/response architecture
Structured validation and exception handling
Dockerized environment with PostgreSQL

The project is evolving toward a complete personal finance platform with additional financial workflows and domain features planned for future iterations.

🧠 Domain Direction

Spendly is positioned as a modern personal finance management platform focused on practical and realistic use cases such as:

Expense tracking
Financial organization
Wallet management
Transaction categorization
Financial insights
User-centered financial workflows

The goal is to combine strong backend engineering practices with a domain that reflects real product scenarios.

✅ Progress
Phase 1 — Domain Modeling (Completed)
Entity	Description
Customer	Platform user with authentication data
Wallet	Financial wallet owned by a customer
Transaction	Financial movements and operations
PaymentKey	Payment key representation
LoginAudit	Login activity tracking
Applied concepts
JPA entity relationships
Enum mapping with EnumType.STRING
Domain encapsulation
Bean Validation
Strategic database indexing
Business-oriented entity modeling
Phase 2 — Backend Core (Completed)
Implemented architecture
Repository layer with Spring Data JPA
Service layer with business rules
DTO pattern for API contracts
Dependency Injection
Global exception handling
Structured validation responses
Password encryption with BCrypt
CPF uniqueness validation
🔐 Security & Authentication (Implemented)

Spendly uses stateless JWT authentication with Spring Security.

Security features
JWT-based authentication
Stateless session management
Custom JWT authentication filter
Protected routes
Custom 401 and 403 handlers
Password encryption with BCrypt
User authentication through SecurityContext
Authentication Flow
User logs in using CPF and password
Backend validates credentials
JWT token is generated
Frontend stores token
Protected requests send token via Authorization header
JWT filter validates token on every request
💼 Wallet Management Module (Implemented)

The wallet module was designed around authenticated ownership and financial organization.

Features
Create wallet
List authenticated user wallets
Retrieve wallet by ID
Update wallet data
Deactivate wallet
Ownership validation for protected resources
Backend concepts applied
DTO separation
Custom exceptions
Ownership checks
Centralized exception handling
Validation with Bean Validation
Enum-based categorization
Service-layer business rules
🌐 API Overview
Authentication
Register Customer

POST /customers

Request Body
{
  "name": "Paulo Emilio",
  "cpf": "12345678901",
  "password": "123456",
  "email": "paulo@email.com"
}
Features
CPF uniqueness validation
BCrypt password hashing
Structured validation handling
DTO-based responses
Login

POST /auth/login

Request Body
{
  "cpf": "12345678901",
  "password": "123456"
}
Response
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
Current Authenticated User

GET /customers/me

Headers
Authorization: Bearer <JWT_TOKEN>
Response
{
  "id": 1,
  "name": "Paulo Emilio",
  "email": "paulo@email.com"
}
💳 Wallet Endpoints
Create Wallet

POST /wallets

Request Body
{
  "name": "Main Wallet",
  "walletType": "BANK_ACCOUNT"
}
List Wallets

GET /wallets

Get Wallet By ID

GET /wallets/{id}

Update Wallet

PUT /wallets/{id}

Request Body
{
  "name": "Updated Wallet",
  "walletType": "CREDIT_CARD"
}
Deactivate Wallet

DELETE /wallets/{id}

🌐 Infrastructure (Dockerized)

The backend environment is fully containerized.

Infrastructure setup
Dockerfile for application packaging
Docker Compose orchestration
PostgreSQL container
Isolated container networking
Externalized environment variables
Run with Docker
docker compose up

This guarantees reproducible environments and simplifies local setup.

🧪 Running the Application
Using Docker (Recommended)
Clone repository
git clone https://github.com/pauloemilio-tech/spendly-backend.git
Navigate to project
cd spendly-backend
Create .env file
JWT_SECRET=your_secret
DB_SPENDLY_URL=jdbc:postgresql://postgres:5432/spendly
DB_SPENDLY_USERNAME=postgres
DB_SPENDLY_PASSWORD=postgres
Run application
docker compose up
Running Locally (Without Docker)
./mvnw spring-boot:run
🔗 Related Repository

Frontend Repository:

https://github.com/pauloemilio-tech/spendly-frontend

👤 Author

Paulo Emilio de Toledo Jr.
