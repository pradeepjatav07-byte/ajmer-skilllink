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
   $env:DB_PASSWORD = "skilllink_dev"
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
