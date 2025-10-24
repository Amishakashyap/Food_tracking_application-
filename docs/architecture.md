# Architecture (Android-first)

## App Layers
- UI: Jetpack Compose screens (Profile, Add Food, Daily Summary, History)
- Domain: Use-cases for calculations, validation, and analytics
- Data: Room database (UserProfile, Foods, Entries, EntryNutrition), repositories
- Services: Search/indexing, AI adapters (NLP, barcode, image), sync client (optional)

## Offline-first
- Local DB is the source of truth
- Sync (Phase 2): background with WorkManager; conflict: last-write-wins for entries, per-day merge

## Suggested Packages
- ui/ (screens, components, theming)
- domain/ (usecases, models)
- data/ (entities, dao, repository)
- services/ (ml, barcode, ocr, sync)

## Data Flow
Profile → Targets
Foods + Quantity → Entry → Derived EntryNutrition
Daily rollup → Analytics (consumed/remaining)

## Security & Privacy
- Store only necessary personal data; no medical history
- Cloud sync opt-in; encrypt at rest where supported
- Anonymize analytics if any

## Optional Backend
- Firebase/Firestore (fast start) or Supabase/Postgres (SQL)
- Endpoints/Collections: profiles, foods (shared catalog), entries, corrections

## ASCII Diagram

[Compose UI]
   |  use-cases
[Domain]  <-->  [Services: AI, OCR, Barcode]
   |  repository
[Data: Room/SQLite]
   | (optional sync)
[Cloud: Firestore/Supabase]

## Telemetry (opt-in)
- Crash reporting (Firebase Crashlytics)
- Simple anonymized usage (screen loads, errors) with user consent
