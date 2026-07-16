# ADR-003: Choose Firebase as Backend

**Status:** Accepted

## Context
Need a scalable backend with auth, database, storage, serverless functions, and push notifications.

## Options
1. **Firebase** — auth, db, storage, functions, messaging in one platform
2. **Supabase** — open-source, PostgreSQL, but less mature
3. **Custom backend** — full control but high maintenance

## Decision
Use **Firebase** (Google Firebase). Key factors:
- Auth built-in (email, Google, Apple)
- Firestore real-time sync
- Storage for images
- Cloud Functions for admin operations
- Cloud Messaging for push notifications
- Emulator suite for local development

## Consequences
- Positive: rapid development, zero server management
- Positive: Firestore real-time listeners simplify chat and feed
- Negative: vendor lock-in, costs scale with usage
- Negative: NoSQL limits complex queries
- Mitigation: keep critical logic in Functions for portability

## Related
- Firestore rules: `apps/admin-web/firestore.rules`
- Storage rules: `storage.rules`
