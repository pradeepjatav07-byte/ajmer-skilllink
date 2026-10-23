# Architecture

## Overview

```text
┌────────────────────────────────────────────────────────────┐
│ React 18 + Vite (frontend/)                                 │
│ Customer / Professional / Admin portals · accessible UI    │
│ Offline demo mode: localStorage                             │
│ API mode: bearer token + relative /api requests             │
└───────────────────────┬────────────────────────────────────┘
                        │ same-origin Vite proxy in development
                        ▼
┌────────────────────────────────────────────────────────────┐
│ Spring Boot 3 / Java 17+ (backend/)                         │
│ REST controllers → services → Spring Data JPA repositories  │
│ DTO validation · Spring Security · JWT + refresh rotation  │
│ Flyway migrations · OpenAPI/Swagger · global problem errors│
└───────────────────────┬────────────────────────────────────┘
                        │ JDBC / UTC timestamps
                        ▼
┌────────────────────────────────────────────────────────────┐
│ MySQL 8.x                                                   │
│ users · profiles · services · FairQuote · bookings         │
│ status history · reviews · complaints · audit · notices    │
└────────────────────────────────────────────────────────────┘
```

## Frontend

- Vite / React 18, JavaScript modules, reusable CSS and inline SVG icons.
- Three navigation sets are isolated by role. In the offline demo, a role-switch menu lets reviewers explore all portals. In API mode, the backend role is authoritative and the UI switch is hidden.
- `frontend/src/data.js` is the source of fictional browser-demo records. `frontend/src/api.js` is an opt-in API client controlled by `VITE_API_MODE`.
- The demo stores the evolving booking, review, complaint, provider and saved-provider state in browser `localStorage`. API mode sends the core auth, discovery, booking, status, review, complaint, availability and admin verification actions to Spring Boot.
- Vite proxies relative `/api`, `/swagger-ui` and `/v3/api-docs` requests to the local backend; browser code does not call a second localhost service directly.
- The official supplied Nexoraa PNG is stored under `frontend/public/branding/` and referenced as an image asset. The original copy is also in `public-assets/branding/`; a symbol-only crop is included for compact marks. Image sizing uses `object-fit: contain` for the full logo.

## Backend layers

- **API / DTO:** request validation, response DTOs, role-secured endpoints, documented OpenAPI.
- **Services:** authentication and rotating refresh tokens; FairQuote rule lookup; booking workflow and visibility/ownership; status transition rules.
- **Repositories:** Spring Data JPA query interfaces for indexed MySQL records.
- **Domain:** users/roles, providers, service categories, FairQuote rules, bookings and timeline, work-proof metadata, reviews, complaints, refresh tokens, notifications and audit events.
- **Security:** BCrypt password encoder; short-lived signed JWT access tokens; opaque refresh tokens stored only as SHA-256 hashes; route + method authorization; ownership checks; local development CORS allowlist.
- **Persistence:** Flyway V1 normalized schema and V2 seed. Hibernate validates rather than generating production tables.

## Core booking lifecycle

```text
REQUESTED → ACCEPTED → PROVIDER_ON_THE_WAY → WORK_STARTED
         → WORK_COMPLETED → CUSTOMER_CONFIRMED
                         ↘ DISPUTED
Any cancellable pre-completion state → CANCELLED
```

Every persisted transition writes a `booking_status_history` row with the actor, value, optional note and timestamp. Customer confirmation is only allowed after `WORK_COMPLETED`. Provider status updates are limited to assigned jobs. Customers and providers cannot access a booking belonging to someone else; admins have platform-wide access.

## Trust and pricing logic

- **Skill Passport:** combines fictional profile, skills, area, experience, job count, rating and service reliability indicators.
- **Trust Score:** seeded/configured demonstration value with a visible explanation; it is not a government or legal credential.
- **FairQuote:** service-category/area range plus separate possible visit fee and emergency surcharge. A disclaimer and price factors are returned with each estimate.
- **Emergency Mode:** filters for emergency-ready and available providers; demo response times are estimates only. Dangerous situations should be referred to appropriate local emergency services.
- **Safe Booking:** captures optional safety preferences and shares a summary that omits exact address/contact details.
- **Work Proof:** the backend accepts consent-confirmed metadata and approved HTTPS or `demo:` references. Binary file transfer/storage and malware scanning are future work.

## Important boundaries

No live payment processor, GPS tracking, SMS/email gateway, government verification, real identity-document workflow, emergency-service dispatch, production file-storage service, or legally binding price guarantee is implemented. The local browser demo persists to `localStorage`; API mode persists to MySQL. Do not describe the demo as either real-world verification or a production emergency service.
