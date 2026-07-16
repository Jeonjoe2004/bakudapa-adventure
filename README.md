# Bakudapa Adventure

Platform digital pendakian gunung Indonesia — navigasi, komunitas, keselamatan, dan dokumentasi perjalanan dalam satu aplikasi.

## 🏔️ Fitur Utama

| Feature | Android | iOS | Admin Web |
|---------|---------|-----|-----------|
| Auth (Login/Register/Google/Apple) | ✅ | ✅ | ✅ |
| Daftar & Detail Gunung | ✅ | ✅ | ✅ CRUD |
| Detail Jalur Pendakian | ✅ | ✅ | - |
| Peta & GPS Tracking | ✅ | 🟡 | - |
| Feed Komunitas | ✅ | ✅ | - |
| Chat | ✅ | ✅ | - |
| Profil & Statistik | ✅ | ✅ | - |
| Emergency SOS | ✅ | ✅ | - |
| Badge & Achievement | ✅ | ✅ | - |
| Pengaturan | ✅ | ✅ | - |
| Dashboard Admin | - | - | ✅ |
| Manajemen Gunung (CRUD) | - | - | ✅ |
| Manajemen Pengguna | - | - | ✅ |

## 📱 Tech Stack

**Android** — Kotlin, Jetpack Compose, Material3, MVVM + MVI, Hilt, Firebase, Room, DataStore, Maplibre, Coil

**iOS** — SwiftUI, Firebase, MapKit, SPM

**Admin Web** — React 19, TypeScript, Vite, Tailwind v4, Firebase, Recharts

## 🚀 Mulai

### Android
```bash
cd apps/android/BakudapaAdventure
./gradlew assembleDebug
```

### Admin Web
```bash
cd apps/admin-web
npm install
npm run dev
```

### iOS (butuh Mac)
Buka `apps/ios/BakudapaAdventure/` di Xcode.

## 🔥 Firebase Setup

1. Buat project di [Firebase Console](https://console.firebase.google.com)
2. Aktifkan Auth (Email/Password, Google, Apple)
3. Aktifkan Firestore
4. Download `google-services.json` → Android `app/`
5. Download `GoogleService-Info.plist` → iOS
6. **Seed data:**
   ```bash
   node scripts/seed-firestore.mjs
   ```
   (butuh service account key)

## 📚 Dokumentasi

Ada di folder `docs/`:
- [Project Bible](docs/foundation/BA-000-Project-Bible.md)
- [Product Roadmap](docs/planning/BA-007-Product-Roadmap.md)
- [Architecture Decisions](docs/adr/)

## 📋 Status

> Fase 1 Foundation (v1.0) — MVP hampir selesai.
> Lihat [Product Scope](docs/planning/BA-002-Product-Scope.md) untuk detail fitur per fase.

## 👥 Kontribusi

Lihat [CONTRIBUTING.md](CONTRIBUTING.md) dan [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## 📄 Lisensi

[MIT](LICENSE)
