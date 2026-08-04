# Spendly 💰 — Backend

Spendly is a backend system for a modern personal finance management platform built with a strong focus on security, financial consistency, scalable architecture and real-world backend engineering practices.

The project simulates the backend foundation of a real financial product, including authentication, wallet management, income and expense flows, transaction reversal, ownership validation, transactional business rules, authenticated financial dashboard data and integration with a deployed frontend application.

> 🚧 **Project Status:** Active Development / Live Demo Available
> This project is still evolving, but it already has a functional deployed demo environment connected to a cloud-hosted backend and database.

---

# 🌐 Live Deployment

The Spendly backend is currently deployed on an Oracle Cloud Infrastructure VM and connected to a Neon PostgreSQL database.

The public demo is accessed through the frontend application deployed on Vercel:

> 🔗 **Live Frontend Demo:**
> https://spendly-fawn.vercel.app

## Deployment Architecture

```txt
User
↓
Vercel Frontend
↓
Vercel /api Proxy
↓
Spring Boot Backend on OCI VM
↓
Neon PostgreSQL Database
```

## Backend Deployment Notes

* Backend deployed on Oracle Cloud Infrastructure
* Application packaged as a Spring Boot `.jar`
* Backend managed as a `systemd` service
* PostgreSQL database hosted on Neon
* Environment variables managed on the VM
* CORS configured for the deployed Vercel frontend
* Frontend communicates with the backend through Vercel `/api` rewrites
* Public API tested through the deployed frontend demo

This setup allows the project to demonstrate a real full-stack deployment flow while keeping the backend and frontend in separate repositories.

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
* Cloud-based backend deployment
* Real frontend-to-backend integration

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
* CORS configuration for deployed frontend origins

## Database & Infrastructure

* PostgreSQL
* Neon PostgreSQL
* Oracle Cloud Infrastructure VM
* systemd service
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
* Transaction lifecycle with `ACTIVE` and `REVERSED` statuses
* Secure income and expense reversal flow
* Protection against duplicate transaction reversals
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
* Cloud database integration with Neon
* Backend deployment on Oracle Cloud Infrastructure
* Runtime management with `systemd`
* CORS configuration for Vercel frontend integration

The project continues evolving toward a more complete financial management platform with transaction filters and pagination, database migrations, concurrency protection, API documentation, observability and production-readiness improvements planned for future iterations.

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
* Transaction status management with `ACTIVE` and `REVERSED`
* Income transaction reversal
* Expense transaction reversal
* Duplicate reversal protection
* Transaction history preservation after reversal
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

## Transaction Status

### ACTIVE

The transaction is effective and contributes to the user's current financial totals.

### REVERSED

The transaction remains available in the financial history, but its effect on the wallet balance has been reversed.

A reversed transaction cannot be reversed again.

## Financial Rules

* Expense operations cannot exceed wallet balance
* Categories must match transaction type
* Wallets must be ACTIVE to receive transactions
* Wallet balances update automatically
* All financial operations are transactional and atomic
* Financial values use `BigDecimal` precision
* Transactions are always validated against wallet ownership
* New transactions are created with `ACTIVE` status
* Reversing an `INCOME` transaction subtracts its amount from the wallet balance
* An `INCOME` reversal is rejected when the wallet does not have sufficient balance
* Reversing an `EXPENSE` transaction restores its amount to the wallet balance
* A reversed transaction is preserved and marked as `REVERSED`
* A transaction cannot be reversed more than once
* Transaction reversal is restricted to the authenticated owner

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
  "status": "ACTIVE",
  "walletName": "Main Wallet",
  "createdAt": "2026-05-25T20:29:00.910114"
    }
  ]
}
```

## Dashboard Rules

* The summary only returns data from the authenticated user
* `totalBalance` considers only `ACTIVE` wallets
* `walletCount` considers only `ACTIVE` wallets
* `totalIncome` considers only `ACTIVE` income transactions
* `totalExpense` considers only `ACTIVE` expense transactions
* `transactionCount` includes both `ACTIVE` and `REVERSED` transactions
* Recent transactions include both statuses and expose the transaction status
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

# Phase 6 — Cloud Deployment and Frontend Integration (Completed)

The backend was deployed to an Oracle Cloud Infrastructure VM and connected to a Neon PostgreSQL database.

## Deployment features

* Spring Boot `.jar` generated with Maven
* Backend uploaded to OCI VM
* Java runtime configured on the VM
* Environment variables configured outside the source code
* Neon PostgreSQL connection validated
* Public port access configured
* Backend managed by `systemd`
* Frontend Vercel origin configured in CORS
* Integration validated through the deployed frontend

## Production-like flow

```txt
Frontend request
↓
Vercel /api rewrite
↓
OCI public backend endpoint
↓
Spring Security JWT validation
↓
Service layer business rules
↓
Neon PostgreSQL
```

## Deployment concepts applied

* Externalized configuration
* Cloud-hosted database
* Public API deployment
* Service process management with `systemd`
* CORS configuration for deployed frontend origins
* Separation between frontend and backend repositories
* Manual production-style validation through real API requests

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
* CORS configured for allowed frontend origins

---

# 🔄 Authentication Flow

1. User logs in using CPF and password
2. Backend validates credentials
3. JWT token is generated
4. Frontend stores token
5. Protected requests send token through the Authorization header
6. JWT filter validates token on every request
7. Authenticated endpoints resolve the current user from the token
8. Protected resources are filtered by authenticated user ownership

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

## Reverse Transaction

```http
POST /transactions/{id}/reverse
```

Reverses the financial effect of an existing transaction while preserving its original record.

### Headers

```http
Authorization: Bearer <JWT_TOKEN>
```

### Response

```json
{
  "id": 1,
  "walletId": 1,
  "walletName": "Main Wallet",
  "type": "EXPENSE",
  "category": "FOOD",
  "amount": 100.00,
  "description": "Market",
  "status": "REVERSED",
  "createdAt": "2026-08-04T10:30:00"
}
```

### Reversal rules

* Reversing an `INCOME` transaction subtracts the transaction amount from the wallet balance
* The reversal is rejected if the current balance is insufficient
* Reversing an `EXPENSE` transaction restores the amount to the wallet balance
* A transaction can only be reversed once
* Transactions from another customer cannot be accessed or reversed

### Possible responses

* `200 OK` — transaction reversed successfully
* `400 Bad Request` — insufficient balance to reverse an income transaction
* `401 Unauthorized` — authentication is missing or invalid
* `404 Not Found` — transaction does not exist or belongs to another customer
* `409 Conflict` — transaction has already been reversed

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

# 🌐 Infrastructure

The backend supports both local Docker-based development and a deployed cloud environment.

## Local infrastructure

* Dockerfile for backend packaging
* Docker Compose orchestration
* PostgreSQL container
* Isolated container networking
* Externalized environment variables
* Persistent PostgreSQL volume

## Deployed infrastructure

* Oracle Cloud Infrastructure VM
* Spring Boot `.jar` running with `systemd`
* Neon PostgreSQL database
* Environment variables stored on the VM
* Public backend endpoint consumed by the deployed frontend
* CORS configured for Vercel frontend domains

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

## Environment variables

Use [`.env.example`](.env.example) as the deployment reference. Configure the required variables locally, in Docker Compose, or in the deployed VM environment.

Do not commit the real `.env` file or production credentials.

For a public frontend, replace `APP_CORS_ALLOWED_ORIGINS` with its deployed URL. Multiple origins must be separated by commas.

## Create `.env`

For local Docker Compose, `DB_SPENDLY_DATABASE` is also required because the JDBC URL is assembled by the Compose configuration.

```env
JWT_SECRET=replace_with_a_strong_secret_at_least_32_bytes

DB_SPENDLY_DATABASE=spendly
DB_SPENDLY_USERNAME=postgres
DB_SPENDLY_PASSWORD=replace_with_database_password
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174
SPRING_JPA_SHOW_SQL=false
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

# ☁️ Deployment Notes

The current deployed backend runs as a Java process managed by `systemd`.

## Build application

```bash
./mvnw clean package -DskipTests
```

Generated artifact:

```txt
target/spendly-0.0.1-SNAPSHOT.jar
```

## Upload JAR to OCI VM

```bash
scp -i path/to/private-key.key \
  target/spendly-0.0.1-SNAPSHOT.jar \
  ubuntu@<OCI_PUBLIC_IP>:/home/ubuntu/spendly.jar
```

## Restart service on VM

```bash
ssh -i path/to/private-key.key ubuntu@<OCI_PUBLIC_IP>
sudo systemctl restart spendly.service
sudo systemctl status spendly.service --no-pager
```

## Check logs

```bash
sudo journalctl -u spendly.service -f
```

## Validate API

Protected endpoint without token should return `401 Unauthorized`:

```bash
curl -i http://<OCI_PUBLIC_IP>:8080/customers/me
```

Expected behavior:

```txt
HTTP/1.1 401
```

---

# 🧪 Testing Status

The backend currently has an automated test suite covering domain rules, security configuration, HTTP behavior and repository integration with PostgreSQL.

## Current result

```text
Tests run: 35
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## Test coverage

The current suite includes:

* Transaction domain status tests
* Transaction service tests
* Wallet service tests
* Dashboard service tests
* Security authentication tests
* CORS configuration tests
* Transaction reversal controller tests with MockMvc
* Repository integration tests with PostgreSQL and Testcontainers
* Main Spring application context test

## Transaction reversal coverage

The suite validates:

* New transactions start with `ACTIVE` status
* Transactions can transition to `REVERSED`
* Income reversal decreases wallet balance
* Income reversal is rejected when the balance is insufficient
* Expense reversal restores wallet balance
* Duplicate reversals are rejected
* Transaction ownership remains isolated between customers
* HTTP responses for `200`, `400`, `401`, `404` and `409`
* Reversed transactions are excluded from income and expense totals
* Reversed transactions remain included in transaction history
* Recent transactions include both `ACTIVE` and `REVERSED` statuses

## Integration environment

Repository integration tests use:

* Testcontainers
* PostgreSQL 17
* An isolated temporary database
* Automatic container startup and cleanup

Run the complete suite with:

```bash
./mvnw clean test
```

On Windows using Git Bash:

```bash
./mvnw.cmd clean test
```

# 🗺️ Roadmap

Planned next steps include:

* Filters and pagination for transactions
* Concurrency protection for wallet balance updates and transaction reversals
* Transaction editing with safe balance recalculation
* Flyway or Liquibase migrations
* More complete dashboard analytics
* OpenAPI / Swagger documentation
* Additional controller and end-to-end API tests
* CI pipeline for build and tests
* Improved observability and structured logs
* Production-oriented environment configuration
* Future backend HTTPS setup with domain, Nginx and SSL certificate

---

# 🔗 Related Repositories & Live Demo

* [Spendly Frontend](https://github.com/paulojrtoledo/spendly-frontend)
* [Live Frontend Demo](https://spendly-fawn.vercel.app)

---

# 👤 Author

**Paulo Emilio**
Backend / Full-Stack Developer in progress

* GitHub: [paulojrtoledo](https://github.com/paulojrtoledo)

* LinkedIn: [Paulo Emilio](https://www.linkedin.com/)
