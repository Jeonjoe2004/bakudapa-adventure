# Contributing

Terima kasih minat berkontribusi ke Bakudapa Adventure! 🏔️

## Cara Berkontribusi

1. **Fork** repo ini
2. **Branch** dari `main`: `git checkout -b feat/namafitur`
3. **Commit** perubahan: `git commit -m 'feat: tambah fitur X'`
4. **Push** ke branch: `git push origin feat/namafitur`
5. Buka **Pull Request**

## Panduan

- Ikuti struktur kode yang sudah ada (MVVM + MVI di Android, clean architecture)
- Tulis kode di Bahasa Indonesia atau Inggris yang konsisten
- Pastikan build tidak broken
- Untuk fitur baru, tambah dokumentasi di `docs/`
- Gunakan conventional commits: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`

## Area Kontribusi

- **Android** — `apps/android/` (Kotlin, Jetpack Compose)
- **iOS** — `apps/ios/` (SwiftUI)
- **Admin Web** — `apps/admin-web/` (React, TypeScript)
- **Backend** — `backend/` (Firebase Functions, TypeScript)
- **Dokumentasi** — `docs/`

## Lapor Bug

Buka issue dengan template yang tersedia. Sertakan:
- Langkah reproduksi
- Expected vs actual behavior
- Screenshot bila perlu
- Device/OS version
