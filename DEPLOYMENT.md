# Backend Deployment Checklist

This deployment is intended for a portfolio demo under active development.

## Prerequisites

- Create a remote PostgreSQL database.
- Configure all required environment variables in the deployment platform.
- Generate a strong `JWT_SECRET` with at least 32 bytes.
- Set `APP_CORS_ALLOWED_ORIGINS` to the public frontend URL.
- Choose either the existing `Dockerfile` or the platform's Maven/JAR flow.

## Required Environment Variables

Use [`.env.example`](.env.example) as a reference. Never commit real secrets.

```env
JWT_SECRET=replace_with_a_strong_secret_at_least_32_bytes
DB_SPENDLY_URL=jdbc:postgresql://host:5432/database
DB_SPENDLY_USERNAME=replace_with_database_user
DB_SPENDLY_PASSWORD=replace_with_database_password
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174,https://spendly-fawn.vercel.app
SPRING_JPA_SHOW_SQL=false
```

`DB_SPENDLY_URL` must use the JDBC format:

```text
jdbc:postgresql://host:port/database
```

If the database provider supplies a regular PostgreSQL URL, convert it to the
JDBC format before configuring the backend.

## Database Schema

`spring.jpa.hibernate.ddl-auto=update` is temporarily acceptable for this
demo. Before treating the application as production-ready, replace automatic
schema updates with versioned migrations using Flyway or Liquibase.

## Post-Deployment Test

Set `BACKEND_URL` to the deployed backend URL and use credentials for an
existing demo user.

1. Authenticate:

```bash
curl -X POST "$BACKEND_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"cpf":"00000000000","password":"000000"}'
```

2. Copy the returned `token` and request the protected dashboard:

```bash
curl "$BACKEND_URL/dashboard/summary" \
  -H "Authorization: Bearer replace_with_returned_token"
```

A successful authenticated response confirms that the deployment can reach
PostgreSQL and that JWT authentication is working.
