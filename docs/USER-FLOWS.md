# User flows

## Customer: find → compare → book → confirm

1. Start on the Customer overview and choose a service or search a local term such as electrician, AC repair or plumber.
2. Filter Ajmer providers by neighbourhood, rating, availability, skill verification, emergency availability or female-professional preference.
3. Open a professional’s Skill Passport. Review skills, service area, completed work, reviews, on-time rate, cancellation rate and the explained Trust Score.
4. Compare up to three professionals. Review the indicative FairQuote range, possible visit fee and emergency fee before booking.
5. Send a normal or emergency request with description, visit preferences and safety options. Emergency requests show a danger-safety disclaimer and cancel path.
6. Use the booking timeline to track requested, accepted, on-the-way, work-started and work-completed updates. Exact location and trusted-contact data are not included in the safe share summary.
7. Review work, confirm completion and submit a star rating/review. Raise a concern from a booking if needed.

## Professional: request → status → reputation

1. Sign in to the Professional portal; review profile completeness and current availability.
2. Inspect a new request, customer area, requested visit time and indicative range.
3. Accept or decline. Keep the customer informed with status steps: accepted, provider on the way, work started, work completed.
4. Add consent-confirmed before/after work-proof metadata where appropriate; this prototype does not upload binary files.
5. Maintain skills, service area, working hours and demo portfolio details in the Skill Passport editor.
6. Review ratings and illustrative performance insights. Demo earnings are not real payments or payout records.

## Admin: verify → operate → learn

1. Sign in to the Admin portal and review KPI counts, emergency requests, open complaints and area/category activity.
2. In the verification queue, inspect fictional profile/skill data; approve, reject or return a profile to review. Important backend decisions write an audit log.
3. Search customer/provider records. Export visible tables as CSV where available.
4. Configure FairQuote ranges and separate fees. Explain that estimates are not guarantees.
5. Triage complaints, update their status and review reported feedback.
6. Monitor booking and service analytics. All demo metrics are labelled as illustrative.

## Safe booking share

- Customer opens booking details → “Share safe booking summary” → copies the visit summary.
- The copied summary includes booking code, service, professional, area, date/time, status and emergency flag.
- Exact street address, phone number and private contact details are deliberately omitted. The browser preview does not send SMS/email or contact a trusted person automatically.

## State and persistence

- **Offline browser demo:** state persists in `localStorage` on the current browser/device. Reset by clearing site data or use the application’s browser storage key `ajmer-skilllink-demo-v1`.
- **API mode:** log in against Spring Boot; booking, status, review, complaint, verification and availability operations use protected API endpoints and persist in MySQL. Backend permissions and ownership checks are enforced independently of the UI.
