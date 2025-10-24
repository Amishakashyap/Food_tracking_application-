# FoodTracker Android App (MVP)

Kotlin + Jetpack Compose + Room, using a prebuilt SQLite food catalog with per-100g nutrition. Users pick a single food per entry from search, enter grams, and see daily consumed vs targets (Calories, Protein, Fat, Carbs) plus Fiber and Sodium.

## Quick start

1) Build the on-device DB from your CSV (already done in the root project):
```powershell
python ..\scripts\build_food_db.py --csv ..\cleaned_nutrition_dataset_per100g.csv --out .\app\src\main\assets\databases\food_catalog.db
```

2) Open this `android-app` folder in Android Studio (Giraffe+ recommended), let it sync Gradle.

3) Run on a device/emulator.

- The app ships with Room configured to load the DB asset via `createFromAsset("databases/food_catalog.db")`.
- Search starts with simple `LIKE` query on `name_normalized`. FTS5 query utility is included in `FoodDao` if you want to switch.

## Scope implemented
- Grams-only entries.
- English food names (use `name_normalized`).
- Track and show: Calories, Protein, Fat, Carbs, Fiber, Sodium.
- Simple screens: Summary, Add Food, Profile.

## Where to add logic next
- Hook Add Food search to `FoodDao` and list results.
- On select + grams, compute nutrition (value_per100g * grams / 100) and insert into `Entry` with meal type.
- Load entries for the current date in `SummaryScreen`, sum macros, and display consumed vs targets with remaining.
- In `ProfileScreen`, save user data to `user_profile` and compute targets with `domain/Targets.kt`.

## Notes
- The database uses FTS5. Most modern Android devices support it; if you hit a device issue, fall back to the `LIKE` query.
- All nutrition fields are per 100g. Treat nulls as zero during computation.
- No training model is needed for MVP; database lookup provides exact nutrition.
