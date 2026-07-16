# Android — Bakudapa Adventure

Android app dibangun dengan **Kotlin + Jetpack Compose + Material3**.

## Stack

- MVVM + MVI architecture
- Hilt (DI)
- Firebase Auth, Firestore, Storage
- Room (local DB)
- Maplibre (maps)
- Coil (image loading)

## Struktur

```
app/src/main/java/com/bakudapa/adventure/
├── core/          — base class, theme, components, utils
├── data/          — local DB, Firebase managers, repository impl
├── di/            — Hilt modules
├── domain/        — repository interfaces, models
└── feature/       — fitur (auth, home, map, feed, chat, dll)
```

## Build

```bash
./gradlew assembleDebug
```
