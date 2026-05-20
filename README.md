# Spendly 💰 — Backend

Spendly is a backend system for a personal finance management platform under active development, designed with a focus on security, scalability, clean architecture and real-world financial workflows.

The project is being built as a realistic backend application, emphasizing authentication, ownership validation, domain organization and maintainable software engineering practices.

🚧 Project Status: Active Development

## 🎯 Overview

Spendly aims to simulate the backend architecture of a modern financial platform, focusing on:

* Secure authentication and authorization
* User-based resource ownership
* Scalable and maintainable architecture
* Clean separation of concerns
* Business validation and domain consistency
* Real-world API design practices

---

## 🚀 Tech Stack

* Java 17+
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* Bean Validation
* JWT Authentication
* PostgreSQL
* Hibernate / JPA
* Maven
* Lombok
* Docker
* Docker Compose

---

## 🧱 Current Stage

The backend currently includes:

* Full JWT authentication flow
* Protected routes with Spring Security
* Customer registration and login
* Wallet management module
* Financial transaction module
* Ownership validation for authenticated users
* DTO-based request/response architecture
* Structured validation and exception handling
* Dockerized environment with PostgreSQL
* Automatic wallet balance management

The project is evolving toward a complete personal finance platform with additional financial workflows and domain features planned for future iterations.

---

## 🧠 Domain Direction

Spendly is positioned as a modern personal finance management platform focused on practical and realistic use cases such as:

* Expense tracking
* Financial organization
* Wallet management
* Financial transactions
* Transaction categorization
* Financial insights
* User-centered financial workflows

The goal is to combine strong backend engineering practices with a domain that reflects real product scenarios.

---

# ✅ Progress

## Phase 1 — Domain Modeling (Completed)

| Entity      | Description                            |
| ----------- | -------------------------------------- |
| Customer    | Platform user with authentication data |
| Wallet      | Financial wallet owned by a customer   |
| Transaction | Financial movements and operations     |
| PaymentKey  | Payment key representation             |
| LoginAudit  | Login activity tracking                |

### Applied concepts

* JPA entity relationships
* Enum mapping with EnumType.STRING
* Domain encapsulation
* Bean Validation
* Strategic database indexing
* Business-oriented entity modeling

---

## Phase 2 — Backend Core (Completed)

### Implemented architecture

* Repository layer with Spring Data JPA
* Service layer with business rules
* DTO pattern for API contracts
* Dependency Injection
* Global exception handling
* Structured validation responses
* Password encryption with BCrypt
* CPF uniqueness validation

---

## Phase 3 — Financial Transactions (Completed)

### Implemented features

* Financial transaction flow
* Income and expense operations
* Automatic wallet balance updates
* Ownership validation for transactions
* Transaction creation and listing
* Insufficient funds validation
* Transaction-based business rules
* Atomic financial operations with @Transactional

### Backend concepts applied

* Transactional consistency
* Financial domain validation
* Atomic persistence operations
* Ownership validation at repository level
* BigDecimal financial precision
* Domain-specific exceptions
* Encapsulated wallet balance management

---

## 🔐 Security & Authentication (Implemented)

Spendly uses stateless JWT authentication with Spring Security.

### Security features

* JWT-based authentication
* Stateless session management
* Custom JWT authentication filter
* Protected routes
* Custom 401 and 403 handlers
* Password encryption with BCrypt
* User authentication through SecurityContext

### Authentication Flow

1. User logs in using CPF and password
2. Backend validates credentials
3. JWT token is generated
4. Frontend stores token
5. Protected requests send token via Authorization header
6. JWT filter validates token on every request

---

## 💼 Wallet Management Module (Implemented)

The wallet module was designed around authenticated ownership and financial organization.

### Features

* Create wallet
* List authenticated user wallets
* Retrieve wallet by ID
* Update wallet data
* Deactivate wallet
* Ownership validation for protected resources

### Backend concepts applied

* DTO separation
* Custom exceptions
* Ownership checks
* Centralized exception handling
* Validation with Bean Validation
* Enum-based categorization
* Service-layer business rules
* Soft delete strategy

---

## 💸 Financial Transactions Module (Implemented)

The transaction module is responsible for handling financial movements linked to user wallets.

### Features

* Create financial transactions
* Income operations
* Expense operations
* Automatic wallet balance updates
* List authenticated user transactions
* Retrieve transaction by ID
* Insufficient funds protection
* Ownership validation for financial operations

### Transaction Types

* INCOME
* EXPENSE

### Financial Rules

* Expense operations cannot exceed wallet balance
* All financial values use BigDecimal precision
* Wallet balances are updated automatically
* Financial operations are transactional and atomic

---

## 🌐 API Overview

# Authentication

## Register Customer

POST /customers

### Request Body

```json
{
  "name": "Paulo Emilio",
  "cpf": "12345678901",
  "password": "123456",
  "email": "paulo@email.com"
}
```

### Features

* CPF uniqueness validation
* BCrypt password hashing
* Structured validation handling
* DTO-based responses

---

## Login

POST /auth/login

### Request Body

```json
{
  "cpf": "12345678901",
  "password": "123456"
}
```

### Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```

---

## Current Authenticated User

GET /customers/me

### Headers

```http
Authorization: Bearer <JWT_TOKEN>
```

### Response

```json
{
  "id": 1,
  "name": "Paulo Emilio",
  "email": "paulo@email.com"
}
```

---

# 💳 Wallet Endpoints

## Create Wallet

POST /wallets

### Request Body

```json
{
  "name": "Main Wallet",
  "walletType": "BANK_ACCOUNT"
}
```

---

## List Wallets

GET /wallets

---

## Get Wallet By ID

GET /wallets/{id}

---

## Update Wallet

PATCH /wallets/{id}

### Request Body

```json
{
  "name": "Updated Wallet",
  "walletType": "CREDIT_CARD"
}
```

---

## Deactivate Wallet

DELETE /wallets/{id}

---

# 💸 Transaction Endpoints

## Create Transaction

POST /transactions

### Request Body

```json
{
  "walletId": 1,
  "type": "INCOME",
  "amount": 1000,
  "description": "Salary"
}
```

---

## List Transactions

GET /transactions

---

## Get Transaction By ID

GET /transactions/{id}

---

## 🌐 Infrastructure (Dockerized)

The backend environment is fully containerized.

### Infrastructure setup

* Dockerfile for application packaging
* Docker Compose orchestration
* PostgreSQL container
* Isolated container networking
* Externalized environment variables

### Run with Docker

```bash
docker compose up
```

This guarantees reproducible environments and simplifies local setup.

---

## 🧪 Running the Application

### Using Docker (Recommended)

#### Clone repository

```bash
git clone https://github.com/paulojrtoledo/spendly-backend.git
```

#### Navigate to project

```bash
cd spendly-backend
```

#### Create .env file

```env
JWT_SECRET=your_secret

DB_SPENDLY_URL=jdbc:postgresql://postgres:5432/spendly
DB_SPENDLY_USERNAME=postgres
DB_SPENDLY_PASSWORD=postgres
```

#### Run application

```bash
docker compose up
```

---

### Running Locally (Without Docker)

```bash
./mvnw spring-boot:run
```

---

## 🔗 Related Repository

Frontend Repository:

https://github.com/paulojrtoledo/spendly-frontend

---

## 👤 Author

Paulo Emilio de Toledo Jr.

