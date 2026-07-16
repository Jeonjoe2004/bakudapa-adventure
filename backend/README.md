# Backend — Firebase Functions

Backend menggunakan Firebase Functions (v2) dengan TypeScript.

## Struktur

- `src/index.ts` — entry point, export semua functions
- `src/auth.ts` — user creation & custom claims
- `src/mountains.ts` — mountain CRUD
- `src/trails.ts` — trail CRUD
- `src/posts.ts` — post moderation
- `src/stats.ts` — dashboard stats & scheduled refresh
- `src/seed.ts` — database seeding
- `src/types.ts` — shared types

## Deploy

```bash
firebase deploy --only functions
```
