# Ajmer SkillLink

**Trusted Local Services, One Click Away** — a responsive local-services marketplace concept for Ajmer, built with a React/Vite frontend and a Java 17+ Spring Boot + MySQL backend. Nexoraa’s supplied logo artwork is included as the original asset and displayed on the splash, login and portal surfaces.

> **Demo notice:** seeded people and service activity are fictional. The browser preview defaults to an offline demo mode that persists actions in `localStorage`. Set `VITE_API_MODE=true` to use the Spring Boot API and MySQL instead. There is no live GPS, payment processing, government verification or emergency dispatch integration.

## Project map

```text
ajmer-skilllink-complete/
├── frontend/                     React 18 + Vite app, branding assets and optional API client
├── backend/                      Spring Boot 3 REST API, JWT/refresh auth, JPA, Flyway
├── database/
│   ├── migrations/               Copy of Flyway schema migration
│   ├── seed/                     Seed SQL plus reproducible export scripts
│   ├── schema.sql                Standalone schema export
│   └── seed/demo-data.sql        Fictional demo seed export
├── data-export/                  JSON exports and data dictionary
├── public-assets/branding/        Original supplied logo and derived symbol crop
├── docs/                         API, database, architecture, user flows and demo script
├── docker-compose.yml            MySQL 8.4 development database
└── .env.example                  Development-only environment values
```

## Requirements

- **Windows 10/11 + VS Code** (or macOS/Linux equivalent)
- JDK **17 or newer** (the project was compiled/tested in this workspace with Java 21 and targets Java 17)
- Node.js 20+ and npm 10+ (Maven 3.9.9 is provided by the checked-in Maven Wrapper)
- Docker Desktop with Compose **or** a local MySQL 8.x server

## Windows + VS Code quick start (PowerShell)

1. Extract `ajmer-skilllink-complete.zip`, open the extracted `ajmer-skilllink-complete` folder in VS Code, and open a terminal there.
2. Copy the development environment template and start MySQL:

   ```powershell
   Copy-Item .env.example .env
   docker compose up -d mysql
   docker compose ps
   ```

   Wait for `ajmer-skilllink-mysql` to become healthy. Flyway applies `V1__schema.sql` and `V2__demo_seed.sql` automatically when the backend first connects.

3. Open **Terminal 1**. Set the local API datasource values and start Spring Boot:

   ```powershell
   cd backend
   $env:DB_URL = "jdbc:mysql://localhost:3306/ajmer_skilllink?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
   $env:DB_USER = "skilllink"
   $env:DB_PASSWORD = "REPLACE_WITH_LOCAL_DB_PASSWORD"
   $env:SERVER_PORT = "8080"
   .\mvnw.cmd spring-boot:run
   ```

   API health: `http://localhost:8080/actuator/health`  
   OpenAPI UI: `http://localhost:8080/swagger-ui.html`

4. Open **Terminal 2** from the project root. Enable the API-connected React mode and start Vite:

   ```powershell
   Copy-Item frontend/.env.example frontend/.env.local
   (Get-Content frontend/.env.local) -replace 'VITE_API_MODE=false','VITE_API_MODE=true' | Set-Content frontend/.env.local
   cd frontend
   npm install
   npm run dev
   ```

   Open the Vite URL shown in the terminal (normally `http://localhost:5173`). Vite proxies `/api` to Spring Boot so browser code uses same-origin relative paths.

### Offline browser demo (no MySQL required)

To preview the React app without starting MySQL or Spring Boot, leave `VITE_API_MODE=false` (or do not create `frontend/.env.local`), then run:

```powershell
cd frontend
npm install
npm run dev
```

This mode is fully interactive and persists demo bookings, status changes, verification actions, saved providers, reviews and complaints in browser `localStorage`. Use the profile menu to switch between the Customer, Professional and Admin demo portals. This is a **browser demo**, not a MySQL-backed session.

## Demo accounts

All accounts use the development-only password **`SkillLink@123`**:

| Role | Email |
|---|---|
| Admin | `admin@ajmerskilllink.demo` |
| Customer | `customer@ajmerskilllink.demo` |
| Service provider | `provider@ajmerskilllink.demo` |

Change all demo credentials and the JWT secret before any non-demo deployment. Do not reuse the documented password.

## Database reset and reseed

**PowerShell / Docker Compose** — destructive; this removes the local MySQL volume and all local data:

```powershell
docker compose down --volumes --remove-orphans
docker compose up -d mysql
```

Restart the Spring Boot application. Flyway re-runs the migrations and fictional seed data. For a local MySQL installation, drop and recreate the `ajmer_skilllink` database, then start the API; do not run `V2__demo_seed.sql` manually after Flyway has recorded it. The exported SQL can also be applied manually to an empty MySQL 8 database in migration order.

## Quality checks

From the project root:

```powershell
cd frontend
npm run build
cd ..\backend
.\mvnw.cmd test
.\mvnw.cmd -DskipTests package
```

The backend tests include Spring context startup against an H2 MySQL-mode test database and booking-transition unit tests. Production/development persistence uses MySQL 8.x with Flyway; the H2 test profile does not replace the MySQL migration run.

## What works in the prototype

- Customer search, category filters, Ajmer-area filters, saved professionals and compare up to three.
- Provider Skill Passport detail, verification badges, transparent Trust Score factors and FairQuote range with visit/emergency fees.
- Normal and emergency request flows, safety preferences, trusted-contact summary sharing, booking timeline, work-proof metadata, completion confirmation, reviews and complaints.
- Provider portal: incoming requests, accept/decline, availability, status progression, profile completeness and performance metrics.
- Admin portal: KPI dashboard, verification queue, provider/customer table, editable FairQuote rules, booking/complaint views, analytics and CSV export.
- Role enforcement on protected backend endpoints, BCrypt password hashes, JWT access tokens plus rotating opaque refresh tokens stored as SHA-256 hashes, DTO validation, global problem responses and Swagger/OpenAPI.

The local preview uses realistic but synthetic demo records; API mode uses the seeded MySQL records after sign-in. Certain less-central views (such as portfolio image upload, live notifications delivery, payout, and multi-provider real-time emergency dispatch) are demonstration metadata only and are not claimed as production integrations.

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [API reference and end-to-end requests](docs/API.md)
- [Database schema and reset guide](docs/DATABASE.md)
- [User flows](docs/USER-FLOWS.md)
- [Product demo script and pitch](docs/DEMO-SCRIPT.md)
- [Data dictionary and record relationships](data-export/README.md)
- [License and fictional-data notice](LICENSE-AND-DATA-NOTICE.md)

<!-- SUBMISSION_ADDENDUM_START -->

## Identity 2026 BCA2 submission supplement

### Problem statement

People in Ajmer often depend on random phone contacts, WhatsApp groups or personal references to find electricians, plumbers, mechanics, tutors, computer-repair professionals and other skilled workers. They cannot easily compare reliability, availability, expected price or safety. Capable local professionals also lack a trustworthy digital identity and a consistent way to receive customers.

**Ajmer SkillLink** fixes this frustration with trusted local discovery, Skill Passports, transparent FairQuote estimates, booking and status tracking, emergency matching, safety preferences, work-proof metadata, notifications and admin moderation.

> Mentor approval for the “your own problem” option: **TO BE CONFIRMED — replace this line with the actual approval status before final submission. Do not claim approval without evidence.**

### How to run

See the main “Windows + VS Code quick start” section above. For a clean local setup:

```powershell
Copy-Item .env.example .env
# Edit .env and replace change-me placeholders.
. .\scripts\dev-env.ps1
docker compose up -d mysql
cd backend
.\mvnw.cmd spring-boot:run
```

In a second terminal from the project root:

```powershell
Copy-Item frontend\.env.example frontend\.env.local
# Set VITE_API_MODE=true in frontend\.env.local
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. Vite proxies same-origin `/api` requests to the Java backend.

### Quality checks

```powershell
. .\scripts\dev-env.ps1
. .\scripts\verify-submission.ps1
```

This runs `npm run build`, `mvn test`, checks required submission files and reports tracked backup/secret-like files.

### Screenshots

Save real screenshots in `docs/screenshots/` using the filenames listed in [docs/screenshots/README.md](docs/screenshots/README.md). Suggested evidence:

| Area | Suggested file |
|---|---|
| Login / registration | `docs/screenshots/01-login.png` |
| Customer overview and search | `docs/screenshots/02-customer-overview.png` |
| FairQuote and booking form | `docs/screenshots/03-fairquote-booking.png` |
| Booking created and notifications | `docs/screenshots/04-booking-notification.png` |
| Provider request and status update | `docs/screenshots/05-provider-workflow.png` |
| Skill Passport editor | `docs/screenshots/06-skill-passport.png` |
| Admin dashboard / verification | `docs/screenshots/07-admin-dashboard.png` |
| Validation error | `docs/screenshots/08-validation-error.png` |
| Tests passing | `docs/screenshots/09-tests-pass.png` |

### Main API endpoints

Full reference: [docs/API.md](docs/API.md).

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Create a customer or provider account |
| POST | `/api/auth/login` | Authenticate and issue JWT/refresh tokens |
| GET | `/api/users/me` | Read the current account |
| GET | `/api/services` | Read active service categories |
| GET | `/api/providers` | Search/filter professionals |
| GET | `/api/providers/{id}/skill-passport` | Read a Skill Passport |
| PUT | `/api/providers/{id}/availability` | Update provider availability |
| GET/POST | `/api/bookings` | Read bookings or create a request |
| GET | `/api/bookings/{id}` | Read one booking |
| POST | `/api/bookings/{id}/status` | Update booking lifecycle status |
| GET/POST | `/api/bookings/{id}/work-proof` | Read or add work-proof metadata |
| POST | `/api/ratings` | Create a review |
| GET/POST | `/api/complaints` | Read or create support tickets |
| GET | `/api/notifications` | Read current-user notifications |
| PATCH | `/api/notifications/{id}/read` | Mark a notification read |
| GET | `/api/admin/dashboard` | Read admin KPIs |
| PATCH | `/api/admin/providers/{id}/verification` | Update provider verification |

These endpoints cover create, read, update and delete-style administrative/customer operations.

### Architecture

One-page diagram: [docs/ARCHITECTURE-DIAGRAM.svg](docs/ARCHITECTURE-DIAGRAM.svg) · [PNG](docs/ARCHITECTURE-DIAGRAM.png) · [PDF](docs/ARCHITECTURE-DIAGRAM.pdf)

Flow: **Browser → JavaScript / React → REST API → Java / Spring Boot → MySQL**.

### Tests

JUnit tests live under `backend/src/test/java`. The test profile uses an in-memory H2 database with Flyway disabled, so running tests does not reset or modify the MySQL demo database.

### Real-user test notes

See [docs/USER-TESTS.md](docs/USER-TESTS.md). **This file must be filled with genuine tester names, quotes and one real feedback-based change before submission.**

### AI Use Declaration

See [docs/AI-USE-DECLARATION.md](docs/AI-USE-DECLARATION.md).

### Commit history note

See [docs/COMMIT-HISTORY-NOTE.md](docs/COMMIT-HISTORY-NOTE.md). The current repository history is documented honestly; commit dates are not fabricated.

### Video

Target filename:

```text
BCA2_PradeepJatav-MoneyRajSingh_AjmerSkillLink.mp4
```

See [docs/VIDEO-SCRIPT.md](docs/VIDEO-SCRIPT.md) for the 5-minute structure. Both team members must be seen or heard; use English voice-over or captions; keep every screen readable for at least 4 seconds.

### Submission ZIP files

Run:

```powershell
. .\scripts\make-submission-zips.ps1
```

This creates `submission-artifacts/ajmer-skilllink-complete.zip` and `submission-artifacts/ajmer-skilllink-data-only.zip`, excluding `node_modules`, build output, local `.env` files, backups and videos.

### Fictional data notice

All seeded people, email addresses, addresses, provider records, ratings, bookings and complaints are fictional demo data. See [LICENSE-AND-DATA-NOTICE.md](LICENSE-AND-DATA-NOTICE.md).

<!-- SUBMISSION_ADDENDUM_END -->

