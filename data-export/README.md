# Fictional data export and dictionary

All exported people, account emails, providers, ratings, bookings, work histories, statuses, neighbourhood-address strings, and complaints in this package are **fictional demo data**. There are no real people, phone numbers, identity numbers, banking details, or identity-document scans. Email addresses use the reserved `.demo` convention; demo street lines are labelled `Fictional demo address`.

## Export inventory and counts

| File | Records | Primary key | Important relationships / fields |
|---|---:|---|---|
| `users.json` | 38 | `id` | 1 admin + 12 customers + 25 providers; `role`, `email`, `area`, `status`; provider `id` is also the provider-profile/user ID |
| `providers.json` | 25 | `id` | `userId` → users; `categoryId` → services; skills, area, experience, verification status, rating, jobs, trust factors, emergency/female/availability flags and estimated range |
| `services.json` | 11 | `id` | Category `slug`; used by provider profiles, bookings and FairQuote rules |
| `bookings.json` | 23 | `id` | 8 active/requested/in-progress and 15 customer-confirmed; `customerId` → users, nullable `providerId` → providers/users, `categoryId` → services; area, visit time, status, emergency flag and indicative range |
| `reviews.json` | 8 | `id` | `bookingId` → completed booking, `customerId` → users, `providerId` → providers; 1–5 rating, review text and reported flag |
| `complaints.json` | 5 | `id` | `customerId` → users, `providerId` → providers, optional `bookingId` → bookings; category, description, status and priority |
| `fair-quote-rules.json` | 11 | `id` | `categoryId` → services; area, estimated low/high, possible visit fee and emergency surcharge; explicit non-guarantee disclaimer |
| `../database/schema.sql` | Schema | table names | MySQL 8 DDL, indexes, foreign keys and constraints (copy of Flyway V1) |
| `../database/seed/demo-data.sql` | Seed script | IDs in SQL | Complete fictional V2 seed (copy of Flyway seed) |

## Key data fields

- **User:** UUID-like ID, full name, demo email, role, Ajmer area and active status. Password hashes are not exposed in the JSON export. The SQL seed contains a BCrypt hash for the documented development password.
- **Provider:** profile/user ID, category and area, years of experience, skills array, fictional profile text, verification status (`VERIFIED`, `UNDER_REVIEW`, etc.), rating average, completed job count, Trust Score (0–100), on-time and cancellation rates, availability and emergency flags. Trust Score is a platform signal, not a government/legal credential.
- **Service:** stable slug, human label, description and indicative ranges sourced from synthetic provider categories.
- **Booking:** display code (`ASL-…`), database UUID, customer/provider/category IDs, requested work, fictional area/address, appointment date/time, lifecycle status, emergency flag and indicative range. Raw private address/phone fields are not included in JSON exports.
- **Review:** booking and party IDs, rating, text, moderation/reported flag and date.
- **Complaint:** booking and party IDs, category, description, status (`OPEN`, `UNDER_REVIEW`, `RESOLVED`) and priority.
- **FairQuote rule:** category/area, low/high base estimate, visit fee and emergency surcharge. These are estimates, never fixed-price promises.

## Relationships at a glance

```text
users ──1:1── customer_profiles
users ──1:1── provider_profiles ──N:1── service_categories
provider_profiles ──1:N── provider_services ──N:1── service_categories
provider_profiles ──1:N── service_areas / provider_availability
users (customer) ──1:N── bookings ──N:1── users (provider)
bookings ──N:1── service_categories
bookings ──1:N── booking_status_history / attachments
bookings ──0:1── ratings_reviews
bookings ──0:N── complaints
service_categories ──1:N── fair_quote_rules
users ──1:N── addresses / trusted_contacts / notifications / refresh_tokens
users (admin) ──1:N── audit_logs / admin_announcements
```

## Regenerate exports

From the project root:

```powershell
node database/seed/generate-demo-seed.mjs
node database/seed/export-demo-json.mjs
```

The scripts import the local source data from `frontend/src/data.js`; no external data sources are used. Review the static JSON and SQL outputs before applying changes to a shared database.
