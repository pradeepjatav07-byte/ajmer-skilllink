# Ajmer SkillLink REST API

Base URL: `http://localhost:8080/api`  
OpenAPI UI: `http://localhost:8080/swagger-ui.html`  
Content type: `application/json` unless a response has no body. Protected routes use `Authorization: Bearer <accessToken>`.

## Authentication and security

- `POST /auth/register` accepts `CUSTOMER` or `PROVIDER`; public registration cannot create an admin.
- `POST /auth/login` returns an access JWT (20-minute default) and an opaque refresh token.
- `POST /auth/refresh` rotates the refresh token and returns a new access/refresh pair. Only a SHA-256 hash of refresh tokens is stored.
- `POST /auth/logout` revokes the supplied refresh token.
- Passwords are hashed using BCrypt. Never log a password, access token, or refresh token.
- Method-level and route-level role checks are applied server-side. Hiding a frontend menu is not authorization.
- CORS defaults to local Vite origins only. Configure an explicit production allowlist before deployment.

## Endpoint groups

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/auth/register` | Public | Create customer/provider account |
| POST | `/auth/login` | Public | Authenticate and issue tokens |
| POST | `/auth/refresh` | Public + refresh token | Rotate refresh token |
| POST | `/auth/logout` | Public + refresh token | Revoke refresh token |
| GET | `/users/me` | Authenticated | Current account DTO |
| GET | `/services` | Public | Active service categories |
| GET | `/providers` | Public | Search with `categorySlug`, `area`, `verified`, `emergency`, `minRating` |
| GET | `/providers/{id}` | Public | Provider profile |
| GET | `/providers/{id}/skill-passport` | Public | Skill Passport DTO |
| GET/PUT | `/providers/{id}/availability` | Public GET; owning provider/admin PUT | Demo availability state |
| GET | `/providers/{id}/ratings` | Public | Review summary rows |
| GET | `/fair-quotes` | Public | Indicative estimate by category and area |
| GET/POST | `/bookings` | Authenticated; create is customer-only | List own/assigned bookings or create a request |
| GET | `/bookings/{id}` | Booking parties/admin | Booking and current status |
| POST | `/bookings/{id}/status` | Booking parties/admin; transition rules apply | Update lifecycle status |
| GET/POST | `/bookings/{id}/work-proof` | Booking parties/admin; provider/admin may add | Consent-checked proof metadata or metadata list |
| GET | `/bookings/{id}/share-summary` | Booking parties/admin | Safe summary excluding exact address/contact data |
| POST | `/ratings` | Customer only | Rate a customer-confirmed booking |
| GET/POST | `/complaints` | Authenticated; create is customer-only | Create/list party-visible support tickets |
| PATCH | `/complaints/{id}/status` | Admin | Update support ticket status |
| GET | `/notifications` | Authenticated | Current-user notifications |
| PATCH | `/notifications/{id}/read` | Owning user | Mark one notification read |
| GET | `/admin/dashboard` | Admin | KPI counts |
| GET | `/admin/providers/verification` | Admin | Verification queue (`?status=UNDER_REVIEW`) |
| PATCH | `/admin/providers/{id}/verification` | Admin | Approve/reject/return to review; writes audit log |
| GET | `/admin/categories` | Admin | Category management list |
| GET | `/admin/fair-quote-rules` | Admin | Active estimate rules |
| PUT | `/admin/fair-quote-rules/{categorySlug}` | Admin | Update Ajmer-wide base, visit-fee and emergency ranges |
| GET | `/admin/analytics` | Admin | Demo service-network metrics |
| GET | `/admin/audit-logs` | Admin | Recent moderation/configuration events |
| GET | `/admin/complaints` | Admin | Full support queue |

`{id}` is the database UUID, not the display `bookingCode`. The UI displays codes such as `ASL-…`; API response `id` is the UUID and `bookingCode` is the readable code.

## Example: complete customer → professional booking flow

These examples use `curl` syntax. Replace values in angle brackets with IDs returned by earlier calls.

### 1) Customer login

```bash
curl -s http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"customer@ajmerskilllink.demo","password":"SkillLink@123"}'
```

Save `accessToken` as `$CUSTOMER_TOKEN`. Use `Authorization: Bearer $CUSTOMER_TOKEN` on protected routes.

### 2) Discover services, providers and a FairQuote

```bash
curl -s http://localhost:8080/api/services
curl -s 'http://localhost:8080/api/providers?categorySlug=electrical&area=Civil%20Lines&verified=true'
curl -s 'http://localhost:8080/api/fair-quotes?categorySlug=electrical&area=Civil%20Lines&emergency=false'
```

A FairQuote contains the base range, possible visit-fee range, emergency-surcharge range, factors and an explicit “final price may vary after inspection” disclaimer. It is not a fixed-price guarantee.

### 3) Create a booking

```bash
curl -s -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" -H 'Content-Type: application/json' \
  -d '{
    "serviceTitle":"Ceiling fan installation",
    "categorySlug":"electrical",
    "areaName":"Civil Lines",
    "addressLabel":"Fictional demo address, Civil Lines, Ajmer",
    "problemDescription":"Install a ceiling fan in the living room; the fan is already available.",
    "preferredTime":"10:00 AM",
    "appointmentDate":"2026-10-16",
    "providerId":"<provider UUID from discovery>",
    "emergency":false,
    "verifiedProviderOnly":true,
    "daytimePreferred":true,
    "femaleProfessionalPreferred":false,
    "shareWithContact":true,
    "trustedContactName":"Demo trusted contact"
  }'
```

The response includes UUID `id`, display `bookingCode`, provider/customer names, category, estimated range, and `REQUESTED` status. A request with no provider ID attempts a best-fit eligible provider match; emergency matching filters emergency-ready, available providers in the selected category/area in demo data.

### 4) Professional accepts and advances the job

```bash
curl -s http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"provider@ajmerskilllink.demo","password":"SkillLink@123"}'
```

Save the response token as `$PROVIDER_TOKEN`. If the provider is assigned to the booking:

```bash
curl -s -X POST http://localhost:8080/api/bookings/<booking UUID>/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" -H 'Content-Type: application/json' \
  -d '{"status":"ACCEPTED","note":"Scope reviewed; visit accepted."}'
```

Valid provider progression is `ACCEPTED` → `PROVIDER_ON_THE_WAY` → `WORK_STARTED` → `WORK_COMPLETED`. The customer may cancel a cancellable booking, dispute after work starts, and confirm only after `WORK_COMPLETED`.

Optional demo work-proof metadata (no binary upload is implemented):

```bash
curl -s -X POST http://localhost:8080/api/bookings/<booking UUID>/work-proof \
  -H "Authorization: Bearer $PROVIDER_TOKEN" -H 'Content-Type: application/json' \
  -d '{"phase":"AFTER","fileName":"demo-finish.jpg","demoUrl":"demo:work-proof-finish","capturedAt":"2026-10-16T11:30:00Z","consentConfirmed":true}'
```

### 5) Customer confirms and reviews

Login as the customer and use the latest customer access token:

```bash
curl -s -X POST http://localhost:8080/api/bookings/<booking UUID>/status \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" -H 'Content-Type: application/json' \
  -d '{"status":"CUSTOMER_CONFIRMED","note":"Work reviewed and confirmed."}'

curl -s -X POST http://localhost:8080/api/ratings \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" -H 'Content-Type: application/json' \
  -d '{"bookingId":"<booking UUID>","rating":5,"reviewText":"Careful work and clear communication."}'
```

For safety sharing, `GET /api/bookings/{id}/share-summary` intentionally does not return the exact address or a phone number.

## Example: admin moderation

```bash
curl -s http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@ajmerskilllink.demo","password":"SkillLink@123"}'

curl -s http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer $ADMIN_TOKEN"

curl -s -X PATCH http://localhost:8080/api/admin/providers/<provider UUID>/verification \
  -H "Authorization: Bearer $ADMIN_TOKEN" -H 'Content-Type: application/json' \
  -d '{"status":"VERIFIED","note":"Fictional demo profile reviewed."}'

curl -s -X PUT http://localhost:8080/api/admin/fair-quote-rules/electrical \
  -H "Authorization: Bearer $ADMIN_TOKEN" -H 'Content-Type: application/json' \
  -d '{"areaName":"Ajmer - all areas","estimatedMin":450,"estimatedMax":900,"visitFeeMin":100,"visitFeeMax":100,"emergencySurchargeMin":150,"emergencySurchargeMax":250}'
```

## Error shape

Validation and application errors use `application/problem+json`, for example:

```json
{
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields need attention.",
  "code": "VALIDATION_ERROR",
  "path": "/api/bookings",
  "timestamp": "2026-10-15T10:00:00Z",
  "fields": { "serviceTitle": "must not be blank" }
}
```

An unauthenticated call returns `401`; a role/ownership violation returns `403`; a disallowed status transition returns `409`. Inputs are validated and server-side role checks are authoritative.
