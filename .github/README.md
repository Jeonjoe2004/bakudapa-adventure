# CI/CD

GitHub Actions workflows untuk bakudapa-adventure:

| Workflow | File | Trigger |
|----------|------|---------|
| **Admin Web** | `.github/workflows/admin-web.yml` | Push ke `main` (build, lint, test) |
| **Backend** | `.github/workflows/backend.yml` | Push ke `main` (tsc, test, build) |
| **Android** | `.github/workflows/android.yml` | Push ke `main` (lint, test, assemble APK) |

## Secrets yang perlu diset di GitHub

- `GOOGLE_SERVICES_JSON` — `google-services.json` buat Android
- `FIREBASE_SERVICE_ACCOUNT` — Firebase Admin SDK key buat deploy