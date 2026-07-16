# iOS App — Bakudapa Adventure

## Setup

1. **Buka di Xcode** (butuh Mac):
   ```bash
   cd apps/ios
   xed .
   ```

2. **Firebase** — taruh `GoogleService-Info.plist` dari Firebase Console di:
   ```
   apps/ios/BakudapaAdventure/GoogleService-Info.plist
   ```

3. **Build & Run**:
   - Pilih target simulator (iOS 17+)
   - `Cmd+R`

## Struktur

```
BakudapaAdventure/
├── BakudapaAdventureApp.swift       ← Entry point + Firebase init
├── Package.swift                    ← SPM dependencies
│
├── Core/
│   ├── Theme.swift                  ← Brand colors, Difficulty enum
│   └── Components.swift             ← Reusable views (Loading, Error, MountainCard)
│
├── Models/
│   └── Models.swift                 ← All Codable models
│
├── Firebase/
│   └── FirebaseManager.swift        ← Firestore + Auth singleton
│
├── Services/
│   ├── AuthViewModel.swift          ← Auth state + login/signup/logout
│   ├── MountainService.swift        ← Mountain CRUD
│   └── TrailService.swift           ← Trail CRUD
│
├── Navigation/
│   └── Navigation.swift             ← AppScreen enum + AuthFlow + MainTab
│
└── Features/
    ├── Auth/                        ← Login, Register, ForgotPassword, Splash
    ├── Home/                        ← Beranda + search
    ├── Mountain/                    ← Detail gunung + trail list
    ├── Trail/                       ← Detail trail + gear + start
    ├── Map/                         ← MapKit
    ├── Feed/                        ← Feed komunitas + post list
    ├── Chat/                        ← Chat rooms + messaging
    ├── Profile/                     ← Profile + sign out
    ├── Emergency/                   ← SOS
    ├── Badge/                       ← Achievement badges grid
    └── Settings/                    ← Dark mode + preferences
```

## Firebase Collections

Pastikan Firestore punya koleksi: `mountains`, `trails`, `users`, `posts`

## Fitur

- Login/Register dengan Firebase Auth
- Navigasi tab (Home, Map, Feed, Chat, Profile)
- Daftar gunung + search
- Detail gunung (hero, stats, trails)
- Detail trail (stats, difficulty, gear, tracking)
- Dark mode di Settings
- Emergency SOS screen
- Feed list with real Firestore data
- Chat room list with unread badges
- Badges/achievements gallery
