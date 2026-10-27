# Helper scripts

Run these from the project root in PowerShell.

- `dev-env.ps1` — dot-source it to load local environment variables and generate a session-only JWT secret.
- `verify-submission.ps1` — frontend build, backend tests and required-file checks.
- `make-submission-zips.ps1` — creates the two final ZIP deliverables in `submission-artifacts/`.

Example:

```powershell
. .\scripts\dev-env.ps1
. .\scripts\verify-submission.ps1
. .\scripts\make-submission-zips.ps1
```
