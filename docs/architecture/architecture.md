# Architecture Overview

## High Level

```
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│  Android App │  │   iOS App   │  │  Admin Web   │
│  Kotlin+MVVM │  │  SwiftUI+MV │  │ React+TS    │
└──────┬───────┘  └──────┬──────┘  └──────┬──────┘
       │                 │                │
       └─────────────────┼────────────────┘
                         │
                  ┌──────┴──────┐
                  │   Firebase   │
                  │ Auth / FS   │
                  │ Storage /   │
                  │ Functions   │
                  └─────────────┘
```

## Android Architecture

- **Pattern:** MVVM + MVI (BaseViewModel → State/Event/Effect)
- **DI:** Hilt
- **UI:** Jetpack Compose + Material3
- **DB:** Room (hiking routes, tracking)
- **Maps:** MapLibre
- **Networking:** Retrofit + OkHttp (OpenWeatherMap API)

## iOS Architecture

- **Pattern:** MVVM + SwiftUI
- **DI:** Manual via ObservableObject + EnvironmentObject
- **UI:** SwiftUI + iOS 17+ APIs
- **Maps:** MapKit
- **DB:** Firestore only (no CoreData yet)

## Admin Web Architecture

- **Pattern:** SPA with routing
- **Framework:** React 19 + TypeScript
- **Build:** Vite 8
- **CSS:** Tailwind v4
- **Charts:** Recharts
- **Auth:** Firebase Auth (email/password)

## Backend Architecture

- **Runtime:** Firebase Functions v2 (Node 22)
- **Language:** TypeScript
- **DB:** Firestore (NoSQL)
- **Scheduled:** Stats refresh every 30 min

## Data Flow

1. Mobile apps read/write Firestore directly (client SDK)
2. Admin web reads/writes Firestore via client SDK
3. Backend functions handle admin-only operations (create mountain, seed data)
4. Scheduled function updates dashboard stats periodically
