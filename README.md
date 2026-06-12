# Spendly 💰 — Backend

Spendly is a backend system for a modern personal finance management platform built with a strong focus on security, financial consistency, scalable architecture and real-world backend engineering practices.

The project simulates the backend foundation of a real financial product, including authentication, wallet management, transaction flows, ownership validation, transactional business rules and authenticated financial dashboard data.

> 🚧 **Project Status:** Active Development
> This project is still evolving and is not production-ready yet.

---

# 🎯 Overview

Spendly aims to model the backend architecture of a real financial platform, emphasizing:

* Secure authentication and authorization
* Financial transaction consistency
* Wallet and balance management
* Ownership validation
* Clean architecture principles
* Domain-driven business rules
* Scalable and maintainable backend structure
* Real-world API design practices
* Authenticated financial summary data for dashboard usage

This repository contains the **backend API** of Spendly.
The frontend is maintained in a separate repository:

> 🌐 **Frontend Repository:**
> [Spendly Frontend](https://github.com/paulojrtoledo/spendly-frontend)

---

# 🚀 Tech Stack

## Core Backend

* Java 17+
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* Hibernate / JPA
* Bean Validation
* Lombok

## Authentication & Security

* JWT Authentication
* BCrypt Password Encryption
* Stateless Authentication
* Protected routes with Spring Security
* Custom unauthorized and forbidden responses

## Database & Infrastructure

* PostgreSQL
* Docker
* Docker Compose

## Build Tool

* Maven

---

# 🧱 Current Stage

The backend currently includes:

* Customer registration
* Login with JWT authentication
* Stateless protected routes
* Current authenticated user endpoint
* Wallet management system
* Wallet ownership validation
* Wallet soft delete strategy
* Financial transaction module
* Income and expense operations
* Automatic wallet balance updates
* Insufficient funds validation
* Transaction category validation by transaction type
* Protection against invalid category/type combinations
* Blocking transactions on inactive wallets
* Authenticated financial dashboard summary endpoint
* Recent transactions summary for dashboard usage
* DTO-based API contracts
* Bean Validation
* Global exception handling
* PostgreSQL database
* Dockerized local environment

The project continues evolving toward a more complete financial management platform with richer dashboards, transaction correction flows, tests, documentation, observability and production-readiness improvements planned for future iterations.

---

# 🧠 Domain Direction

Spendly is positioned as a realistic personal finance management platform focused on practical financial organization workflows.

## Current domain focus

* Wallet management
* Income and expense tracking
* Financial organization
* Transaction categorization
* Balance management
* Financial consistency
* Authenticated financial summaries
* User-centered financial workflows

The project combines backend engineering practices with realistic business scenarios commonly found in financial applications.

---

# ✅ Progress

# Phase 1 — Domain Modeling (Completed)

| Entity      | Description                            |
| ----------- | -------------------------------------- |
| Customer    | Platform user with authentication data |
| Wallet      | Financial wallet owned by a customer   |
| Transaction | Financial operations linked to wallets |
| PaymentKey  | Payment key representation             |
| LoginAudit  | Authentication activity tracking       |

## Applied concepts

* JPA entity relationships
* Enum mapping with `EnumType.STRING`
* Domain encapsulation
* Bean Validation
* Business-oriented entity modeling
* Strategic database indexing

---

# Phase 2 — Backend Core (Completed)

## Implemented architecture

* Repository layer with Spring Data JPA
* Service layer with business rules
* DTO pattern for API contracts
* Dependency Injection
* Global exception handling
* Structured validation responses
* BCrypt password encryption
* CPF uniqueness validation
* Stateless JWT authentication

---

# Phase 3 — Wallet Management (Completed)

The wallet module was designed around ownership validation and financial organization.

## Features

* Create wallet
* Initial wallet balance
* List authenticated user wallets
* Retrieve wallet by ID
* Update wallet data
* Deactivate wallet using soft delete
* Wallet ownership validation
* Active wallet filtering

## Wallet Types

* BANK_ACCOUNT
* CASH
* CREDIT_CARD
* INVESTMENT
* DIGITAL_WALLET

## Backend concepts applied

* Soft delete strategy
* Ownership validation
* Encapsulated balance management
* DTO separation
* Validation with Bean Validation
* Enum-based categorization
* Service-layer business rules

---

# Phase 4 — Financial Transactions (Completed)

The transaction module is responsible for handling financial operations linked to wallets.

## Features

* Create transactions
* Income operations
* Expense operations
* Automatic wallet balance updates
* List authenticated user transactions
* Retrieve transaction by ID
* Insufficient funds protection
* Financial category validation
* Ownership validation for transactions
* Atomic financial operations with `@Transactional`

## Transaction Types

### INCOME

* SALARY
* FREELANCE
* INVESTMENT_RETURN
* GIFT
* OTHER_INCOME

### EXPENSE

* FOOD
* TRANSPORT
* HEALTH
* EDUCATION
* ENTERTAINMENT
* SHOPPING
* BILLS
* INVESTMENT
* OTHER_EXPENSE

## Financial Rules

* Expense operations cannot exceed wallet balance
* Categories must match transaction type
* Wallets must be ACTIVE to receive transactions
* Wallet balances update automatically
* All financial operations are transactional and atomic
* Financial values use `BigDecimal` precision
* Transactions are always validated against wallet ownership

## Backend concepts applied

* Transactional consistency
* Domain validation
* Financial business rules
* Atomic persistence operations
* Ownership validation at repository level
* BigDecimal monetary precision
* Domain-specific exceptions

---

# Phase 5 — Authenticated Financial Dashboard (Completed)

The dashboard module provides summarized financial data for the authenticated user.

## Features

* Authenticated financial summary endpoint
* Total balance across active wallets
* Total income
* Total expense
* Active wallet count
* Transaction count
* Recent transactions list
* User-based data isolation

## Endpoint

```http
GET /dashboard/summary
```

## Example Response

```json
{
  "totalBalance": 6300.00,
  "totalIncome": 6600.00,
  "totalExpense": 100.00,
  "walletCount": 2,
  "transactionCount": 3,
  "recentTransactions": [
    {
      "id": 3,
      "description": "Market",
      "amount": 100.00,
      "type": "EXPENSE",
      "category": "FOOD",
      "walletName": "Main Wallet",
      "createdAt": "2026-05-25T20:29:00.910114"
    }
  ]
}
```

## Dashboard Rules

* The summary only returns data from the authenticated user
* `totalBalance` considers only ACTIVE wallets
* `walletCount` considers only ACTIVE wallets
* Income, expense, transaction count and recent transactions preserve the user's financial history
* Recent transactions are ordered from newest to oldest
* Empty financial data returns zero values and an empty recent transaction list

## Backend concepts applied

* Authenticated data aggregation
* User-based query filtering
* DTO-specific dashboard responses
* Repository-level financial queries
* BigDecimal-safe summary responses
* Separation between dashboard read model and core transaction rules

---

# 🔐 Security & Authentication

Spendly uses stateless JWT authentication with Spring Security.

## Security features

* JWT-based authentication
* Stateless session management
* Custom JWT authentication filter
* Protected routes
* Custom 401 and 403 handlers
* BCrypt password encryption
* Authentication through SecurityContext
* Authenticated resource ownership validation

---

# 🔄 Authentication Flow

1. User logs in using CPF and password
2. Backend validates credentials
3. JWT token is generated
4. Frontend stores token
5. Protected requests send token through the Authorization header
6. JWT filter validates token on every request
7. Authenticated endpoints resolve the current user from the token

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

# 📊 Dashboard Endpoints

## Get Authenticated Financial Summary

```http
GET /dashboard/summary
```

### Headers

```http
Authorization: Bearer <JWT_TOKEN>
```

### Response

```json
{
  "totalBalance": 6300.00,
  "totalIncome": 6600.00,
  "totalExpense": 100.00,
  "walletCount": 2,
  "transactionCount": 3,
  "recentTransactions": [
    {
      "id": 3,
      "description": "Market",
      "amount": 100.00,
      "type": "EXPENSE",
      "category": "FOOD",
      "walletName": "Main Wallet",
      "createdAt": "2026-05-25T20:29:00.910114"
    }
  ]
}
```

---

# 🌐 Infrastructure (Dockerized)

The backend environment is containerized for local development.

## Infrastructure setup

* Dockerfile for backend packaging
* Docker Compose orchestration
* PostgreSQL container
* Isolated container networking
* Externalized environment variables
* Persistent PostgreSQL volume

## Run with Docker

```bash
docker compose up -d --build
```

This helps provide reproducible environments and simplifies local setup.

---

# 🧪 Running the Application

# Using Docker

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

# Running Locally with PostgreSQL on Docker

This is useful during development when running the backend from an IDE such as IntelliJ.

## Start containers

```bash
docker compose up -d
```

## Stop only the backend container if you want to run the API from the IDE

```bash
docker stop spendly-backend
```

## Keep PostgreSQL running

```bash
docker ps
```

Expected PostgreSQL container:

```bash
spendly-postgres   0.0.0.0:5432->5432/tcp
```

## Run backend locally

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
.\mvnw spring-boot:run
```

---

# 🧪 Testing Status

The project currently compiles successfully, but the automated test suite still needs improvement.

Current testing limitations:

* Tests are not yet fully isolated
* Some existing tests depend on a local PostgreSQL instance
* Testcontainers or a dedicated test profile are planned for future iterations

Planned improvements:

* Unit tests for services
* Integration tests for repositories and controllers
* Authentication flow tests
* Financial transaction rule tests
* Dashboard summary tests

---

# 🗺️ Roadmap

Planned next steps include:

* Transaction cancellation flow
* Transaction editing with balance recalculation
* Filters and pagination for transactions
* More complete dashboard analytics
* OpenAPI / Swagger documentation
* Flyway or Liquibase migrations
* Automated tests with isolated database setup
* CI pipeline for build and tests
* Improved observability and structured logs
* Production-oriented environment configuration

---

# 🔗 Related Repositories

* [Spendly Frontend](https://github.com/paulojrtoledo/spendly-frontend)

---

# 👤 Author

**Paulo Emilio**
Backend / Full-Stack Developer in progress

* GitHub: [paulojrtoledo](https://github.com/paulojrtoledo)
* LinkedIn: [Paulo Emilio](https://www.linkedin.com/)
