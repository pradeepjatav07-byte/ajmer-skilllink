# Ajmer SkillLink — database and data-only package

This package contains the MySQL 8 schema, reproducible fictional demo seed, JSON exports, data dictionary, and the supplied Nexoraa logo artwork. It does **not** contain the React application or Spring Boot application source.

## Contents

- `migrations/V1__schema.sql` — normalized MySQL 8 schema; the same file as Flyway migration V1.
- `migrations/V2__demo_seed.sql` — fictional Ajmer demo seed; the same file as Flyway migration V2.
- `schema.sql` — standalone copy of V1; use this **instead of**, not in addition to, migration V1 for manual setup.
- `seed/demo-data.sql` — standalone copy of V2.
- `seed/generate-demo-seed.mjs` and `seed/export-demo-json.mjs` — reproducible export scripts (Node.js; require the project frontend seed source to be present).
- `../data-export/` — JSON tables and the data dictionary with record counts, field definitions, and relationships.
- `../public-assets/branding/` — byte-for-byte original supplied logo and its derived symbol crop.
- `../LICENSE-AND-DATA-NOTICE.md` — supplied-asset and fictional-data limitations.

All demo accounts, names, bookings, reviews, work histories, addresses, and complaints are fictional. The SQL includes a demo BCrypt password hash for `SkillLink@123`; it is for local evaluation only. No real identity, banking, or production credentials are included.

## Apply manually to an empty MySQL 8 database

Run from the extracted package root. Use either the migration pair **or** the standalone schema/seed pair, not both:

```sh
mysql -u root -p -e "CREATE DATABASE ajmer_skilllink CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;"
mysql -u root -p ajmer_skilllink < database/migrations/V1__schema.sql
mysql -u root -p ajmer_skilllink < database/migrations/V2__demo_seed.sql
```

For Spring Boot, let Flyway apply V1 and V2 on first startup; do not manually replay a migration after Flyway records it. To reset a local Compose database, run `docker compose down --volumes --remove-orphans` from the complete project folder, then restart the API so Flyway reseeds the empty volume.

## Rebuild exports

The scripts expect `frontend/src/data.js`, which is present in the complete project archive. From the complete project root:

```sh
node database/seed/generate-demo-seed.mjs
node database/seed/export-demo-json.mjs
```

Review and test generated SQL on a disposable MySQL 8 database before using it anywhere shared. These files are illustrative demo data, not a real Ajmer service directory or price recommendation.
