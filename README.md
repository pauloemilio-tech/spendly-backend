# Spendly 💰 — Backend

Spendly is a backend system for a modern personal finance management platform built with a strong focus on security, financial consistency, scalable architecture and real-world backend engineering practices.

The project simulates the backend foundation of a real financial product, including authentication, wallet management, transaction flows, ownership validation and transactional business rules.

🚧 **Project Status:** Active Development

---

# 🎯 Overview

Spendly aims to model the backend architecture of a real financial platform, emphasizing:

- Secure authentication and authorization
- Financial transaction consistency
- Wallet and balance management
- Ownership validation
- Clean architecture principles
- Domain-driven business rules
- Scalable and maintainable backend structure
- Real-world API design practices

---

# 🚀 Tech Stack

## Core Backend

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Hibernate / JPA
- Bean Validation
- Lombok

## Authentication & Security

- JWT Authentication
- BCrypt Password Encryption
- Stateless Authentication

## Database & Infrastructure

- PostgreSQL
- Docker
- Docker Compose

## Build Tool

- Maven

---

# 🧱 Current Stage

The backend currently includes:

- Full JWT authentication flow
- Protected routes with Spring Security
- Customer registration and login
- Wallet management system
- Financial transaction module
- Automatic wallet balance updates
- Ownership validation
- DTO-based API contracts
- Structured validation and exception handling
- Soft delete strategy for wallets
- Dockerized PostgreSQL environment
- Financial domain validation rules

The project continues evolving toward a complete financial management platform with dashboards, analytics and richer financial workflows planned for future iterations.

---

# 🧠 Domain Direction

Spendly is positioned as a realistic personal finance management platform focused on practical financial organization workflows.

## Current domain focus

- Wallet management
- Income and expense tracking
- Financial organization
- Transaction categorization
- Balance management
- Financial consistency
- User-centered financial workflows

The project combines strong backend engineering practices with realistic business scenarios commonly found in financial applications.

---

# ✅ Progress

# Phase 1 — Domain Modeling (Completed)

| Entity | Description |
|---|---|
| Customer | Platform user with authentication data |
| Wallet | Financial wallet owned by a customer |
| Transaction | Financial operations linked to wallets |
| PaymentKey | Payment key representation |
| LoginAudit | Authentication activity tracking |

## Applied concepts

- JPA entity relationships
- Enum mapping with `EnumType.STRING`
- Domain encapsulation
- Bean Validation
- Business-oriented entity modeling
- Strategic database indexing

---

# Phase 2 — Backend Core (Completed)

## Implemented architecture

- Repository layer with Spring Data JPA
- Service layer with business rules
- DTO pattern for API contracts
- Dependency Injection
- Global exception handling
- Structured validation responses
- BCrypt password encryption
- CPF uniqueness validation

---

# Phase 3 — Wallet Management (Completed)

The wallet module was designed around ownership validation and financial organization.

## Features

- Create wallet
- Initial wallet balance
- List authenticated user wallets
- Retrieve wallet by ID
- Update wallet data
- Deactivate wallet (soft delete)
- Wallet ownership validation
- Active wallet filtering

## Wallet Types

- BANK_ACCOUNT
- CASH
- CREDIT_CARD
- INVESTMENT
- DIGITAL_WALLET

## Backend concepts applied

- Soft delete strategy
- Ownership validation
- Encapsulated balance management
- DTO separation
- Validation with Bean Validation
- Enum-based categorization
- Service-layer business rules

---

# Phase 4 — Financial Transactions (Completed)

The transaction module is responsible for handling financial operations linked to wallets.

## Features

- Create transactions
- Income operations
- Expense operations
- Automatic wallet balance updates
- List authenticated user transactions
- Retrieve transaction by ID
- Insufficient funds protection
- Financial category validation
- Ownership validation for transactions
- Atomic financial operations with `@Transactional`

## Transaction Types

### INCOME

- SALARY
- FREELANCE
- INVESTMENT_RETURN
- GIFT
- OTHER_INCOME

### EXPENSE

- FOOD
- TRANSPORT
- HEALTH
- EDUCATION
- ENTERTAINMENT
- SHOPPING
- BILLS
- INVESTMENT
- OTHER_EXPENSE

## Financial Rules

- Expense operations cannot exceed wallet balance
- Categories must match transaction type
- Wallets must be ACTIVE to receive transactions
- Wallet balances update automatically
- All financial operations are transactional and atomic
- Financial values use `BigDecimal` precision

## Backend concepts applied

- Transactional consistency
- Domain validation
- Financial business rules
- Atomic persistence operations
- Ownership validation at repository level
- BigDecimal monetary precision
- Domain-specific exceptions

---

# 🔐 Security & Authentication

Spendly uses stateless JWT authentication with Spring Security.

## Security features

- JWT-based authentication
- Stateless session management
- Custom JWT authentication filter
- Protected routes
- Custom 401 and 403 handlers
- BCrypt password encryption
- Authentication through SecurityContext

---

# 🔄 Authentication Flow

1. User logs in using CPF and password
2. Backend validates credentials
3. JWT token is generated
4. Frontend stores token
5. Protected requests send token via Authorization header
6. JWT filter validates token on every request

---

# 🌐 API Overview

# Authentication

## Register Customer

```http
POST /customers
```

### Request Body

```json
{
  "name": "Paulo Emilio",
  "cpf": "12345678901",
  "password": "123456",
  "email": "paulo@email.com"
}
```

---

## Login

```http
POST /auth/login
```

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

```http
GET /customers/me
```

### Headers

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# 💼 Wallet Endpoints

## Create Wallet

```http
POST /wallets
```

### Request Body

```json
{
  "name": "Santander",
  "walletType": "BANK_ACCOUNT",
  "initialBalance": 500
}
```

---

## List Wallets

```http
GET /wallets
```

---

## Get Wallet By ID

```http
GET /wallets/{id}
```

---

## Update Wallet

```http
PATCH /wallets/{id}
```

---

## Deactivate Wallet

```http
DELETE /wallets/{id}
```

---

# 💸 Transaction Endpoints

## Create Transaction

```http
POST /transactions
```

### Request Body

```json
{
  "walletId": 1,
  "type": "EXPENSE",
  "category": "FOOD",
  "amount": 100,
  "description": "Market"
}
```

---

## List Transactions

```http
GET /transactions
```

---

## Get Transaction By ID

```http
GET /transactions/{id}
```

---

# 🌐 Infrastructure (Dockerized)

The backend environment is fully containerized.

## Infrastructure setup

- Dockerfile for backend packaging
- Docker Compose orchestration
- PostgreSQL container
- Isolated container networking
- Externalized environment variables
- Persistent PostgreSQL volume

## Run with Docker

```bash
docker compose up -d --build
```

This guarantees reproducible environments and simplifies local setup.

---

# 🧪 Running the Application

# Using Docker (Recommended)

## Clone repository

```bash
git clone https://github.com/paulojrtoledo/spendly-backend.git
```

## Navigate to project

```bash
cd spendly-backend
```

## Create `.env`

```env
JWT_SECRET=your_secret

DB_SPENDLY_DATABASE=spendly
DB_SPENDLY_USERNAME=postgres
DB_SPENDLY_PASSWORD=postgres
```

## Run application

```bash
docker compose up -d --build
```

---

# Running Locally (Without Docker)

```bash
./mvnw spring-boot:run
```

---

# 🔗 Related Repositories

- [Spendly Frontend](https://github.com/paulojrtoledo/spendly-frontend)

---

# 👤 Author

Paulo Emilio de Toledo Jr.
