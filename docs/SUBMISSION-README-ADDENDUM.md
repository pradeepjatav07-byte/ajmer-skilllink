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
