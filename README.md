# Food Tracking Android App

A final-year project: an Android-only (Phase 1) food and nutrition tracking app that computes personalized daily targets and tracks actual consumption via food entries. The app uses a nutrition database (v1) and an AI-assisted lookup/vision pipeline (v2+) to infer macros from user-entered foods.

## Highlights
- Personal profile with science-backed targets (BMR/TDEE, macros, fiber, sodium)
- Daily log by meal (breakfast, lunch, dinner) + snacks
- Macro/micro tracking, progress vs targets, remaining budget
- Offline-first with local storage; optional cloud backup/sync
- AI roadmap: NLP mapping, barcode scan, image recognition

## Tech stack (recommended)
- Android: Kotlin + Jetpack Compose + Room (SQLite) + WorkManager
- Optional backend (Phase 2): Firebase/Firestore or Supabase/Postgres
- AI: On-device ML Kit (barcode/OCR), image model via TensorFlow Lite (later), server-side model as fallback

## Folders
- `docs/` Requirements, architecture, AI plan, nutrition math
- `data/` Sample nutrition data
- `scripts/` Quick prototypes (e.g., target calculator)

## Getting started
1) Read `docs/requirements.md` and `docs/architecture.md`.
2) Run the simple target calculator (optional):
   - Requires Python 3.9+.
   - `python scripts/calc_targets.py --sex male --age 22 --height_cm 175 --weight_kg 70 --activity moderate --goal maintain`
3) Start Android app (to be created next) with Kotlin/Compose, following the contracts in `docs/requirements.md`.

## Roadmap (high level)
- Phase 1 (MVP): Profile, local DB, search foods, log entries, daily analytics
- Phase 2: Cloud sync, reminders, streaks, dashboard widgets, barcode scan
- Phase 3: Image-based nutrition assist, recipe import, recommendations

## Disclaimer
This is not medical advice. For medical conditions or special diets, consult a licensed professional.
