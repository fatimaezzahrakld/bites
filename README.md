# Bites

A food-ordering experience built with Java 21, Spring Boot, PostgreSQL and a responsive vanilla JavaScript frontend.

## Run locally

Install Docker Desktop, then run:

```bash
docker compose up --build
```

Open http://localhost:8080

The database and sample menu are initialized automatically. Orders are persisted in PostgreSQL.

## API

- `GET /api/menu` — available menu items
- `POST /api/orders` — place an order (name, email, items)
- `GET /api/orders/{id}?email=...` — retrieve an order

Example:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Demo Customer","customerEmail":"demo@example.com","items":[{"menuItemId":1,"quantity":2}]}'
```

## Scope

V1 includes browsing, category filters, cart management, checkout and persistent order creation. Currency: MAD. Payments, accounts, driver tracking and restaurant management are **not implemented**. Do not expose the order lookup API as a production customer portal: email-based lookup is only a demo convenience and must be replaced by authentication and authorization. The checkout is a demo, not a payment processor.

Before production: add authentication, Flyway migrations, rate limiting, production secrets, tests, and a hardened deployment configuration.
