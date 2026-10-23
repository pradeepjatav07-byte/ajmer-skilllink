# Database guide

## Engine and migrations

- **Target:** MySQL 8.x (Compose uses MySQL 8.4).
- **ORM:** Spring Data JPA / Hibernate, `ddl-auto: validate` in the normal profile.
- **Migrations:** Flyway under `backend/src/main/resources/db/migration/`.
- `V1__schema.sql` creates the normalized schema, indexes, constraints and foreign keys.
- `V2__demo_seed.sql` loads fictional Ajmer SkillLink records.
- Root exports: `database/schema.sql`, `database/migrations/V1__schema.sql`, `database/seed/demo-data.sql`.
- The API applies Flyway migrations at startup. Do not manually replay V2 into a database already marked as migrated.

## Tables and relationships

| Table | Purpose / key fields | Relationships |
|---|---|---|
| `users` | Identity, unique email, BCrypt hash, `CUSTOMER` / `PROVIDER` / `ADMIN`, active/suspended state, timestamps | Parent for customer/provider profiles and all actor records |
| `customer_profiles` | Preferred language, profile completeness | One-to-one `users` |
| `provider_profiles` | Provider code, primary service, Ajmer area, skills JSON, verification status, rating, job count, transparent trust factors, availability flags | One-to-one `users`; optional primary `service_categories` |
| `service_categories` | Service name, slug, description, active flag | Parent for provider services, bookings and FairQuote rules |
| `provider_services` | A normalized provider-to-skill/category association | Many-to-one provider profile and service category |
| `service_areas` | Provider service neighbourhoods and primary-area flag | Many-to-one provider profile |
| `provider_availability` | Day/time windows and emergency availability | Many-to-one provider profile |
| `addresses` | Customer-saved label, area, fictional/demo address line, default flag | Many-to-one `users` |
| `fair_quote_rules` | Category/area estimated min/max, possible visit fee, emergency surcharge, active flag | Many-to-one `service_categories`; unique category + area |
| `bookings` | Booking code, customer/provider, service, category, area/address label, date/time, status, emergency and safety preferences, estimate | Customer/provider `users`, category, status-history children |
| `booking_status_history` | Status transition, actor, note and timestamp | Many-to-one booking and actor user |
| `attachments` | Work-proof metadata, BEFORE/AFTER phase, demo URL, consent flag and timestamp | Many-to-one booking and uploader user; binary upload is intentionally not implemented |
| `ratings_reviews` | One review per booking, rating 1–5, text, reported flag | One-to-one booking; customer/provider users |
| `complaints` | Ticket category, description, priority/status, optional resolution note | Customer/provider users; optional booking |
| `trusted_contacts` | Display name and consent flag; contact hint only, no phone seeded | Customer user |
| `notifications` | User-scoped event title/body/kind/read timestamp | Many-to-one user |
| `refresh_tokens` | SHA-256 token hash and expiry, never raw token | Many-to-one user |
| `audit_logs` | Admin actor/action/target/details/timestamp | Optional actor user |
| `admin_announcements` | Audience-scoped admin message and publish window | Optional creator user |

UUID-like IDs use 36-character values. Foreign keys, unique keys and indexes protect email, provider codes, booking codes, one-review-per-booking and common lookup paths. Booking status enum values are uppercase with underscores in the database/API; the UI shows readable labels.

## Seed inventory (V2)

- 11 service categories
- 38 user accounts: 1 admin, 12 customers, 25 providers
- 12 customer profiles and fictional Ajmer-area address labels
- 25 provider profiles across Civil Lines, Vaishali Nagar, Panchsheel Nagar, Ana Sagar, Naya Bazaar, Madar Gate and Beawar Road
- 75 provider-skill association rows; 50 provider service-area rows; 25 weekly availability rows
- 11 Ajmer-wide FairQuote rules
- 23 bookings: 8 requested/active/in-progress samples and 15 customer-confirmed jobs
- 109 booking status-history events, 10 before/after proof-metadata rows
- 8 ratings/reviews, 5 complaints, 3 notifications, 1 trusted-contact placeholder, 1 announcement and an initial audit entry

All names, account emails, provider records, neighbourhood/address strings, work histories, status events and reviews are fabricated demo data. No phone numbers, real Aadhaar/PAN/bank details, identity-document scans or real residential addresses are seeded. Demo `address_line` values explicitly say “Fictional demo address”.

## Generate exports again

From the project root, after editing the frontend seed source:

```powershell
node database/seed/generate-demo-seed.mjs
node database/seed/export-demo-json.mjs
```

The first script writes the Flyway V2 seed and the standalone `database/seed/demo-data.sql`; the second refreshes JSON exports in `data-export/`. Both use only Node built-ins plus the local `frontend/src/data.js` module. Review ID relationships and test on an empty MySQL 8 database before promoting any change.

## Reset

With Docker Compose (this permanently deletes the local DB volume):

```powershell
docker compose down --volumes --remove-orphans
docker compose up -d mysql
```

Restart the backend with `cd backend; .\mvnw.cmd spring-boot:run` in PowerShell; Flyway reapplies V1 and V2. To reset an external MySQL instance, back up as needed, drop/recreate `ajmer_skilllink`, then restart the API. Do not use `flyway clean` on a shared or production database.

## Backups and production notes

- Store database credentials and JWT keys in a secret manager, not checked-in `.env` files.
- Add an encrypted backup/restore process and define retention before launch.
- Production must use TLS, a restricted CORS allowlist, non-demo passwords, a strong random JWT secret, rate limits, secure image storage and access controls for addresses, trusted contacts and work-proof data.
- The demo currently stores work-proof URL/name/timestamp/consent metadata only; it does not receive binary uploads or perform malware scanning.
