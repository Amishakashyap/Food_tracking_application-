# Requirements and Scope

## Problem Statement
Help users understand and meet their daily nutrition targets by logging foods and visualizing intake vs personalized goals.

## Personas
- Student/young professional: time-constrained, wants simple tracking and reminders
- Fitness-focused user: wants precise macros and protein targeting
- Health-conscious: wants sugar/sodium limits and fiber goals

## Core Features (MVP)
1) Profile & targets
   - Inputs: sex, age, height, weight, activity level, goal (lose/maintain/gain)
   - Optional: body fat %, dietary preference (veg/vegan/halal/jain), allergies/intolerances, region/cuisine, unit system, time zone
   - Outputs: daily calories (TDEE adjusted), macros (protein/fat/carbs), fiber target, sodium limit, water recommendation
2) Food logging
   - Add foods with quantity (g/ml/servings) categorized as breakfast/lunch/dinner/snack
   - Search database (v1) + AI assist (v2+) to map foods
   - Show macro/micro breakdown for each entry & meal totals
3) Analytics
   - Daily summary: consumed vs target and remaining budget
   - Streaks and compliance (% days within ±10% of calorie target)
   - Weekly trend charts (7-day rolling)

## Nice-to-Have (Phase 2+)
- Barcode scanning, OCR on labels
- Image recognition for common meals
- Reminders, water tracking, Wear OS tile/complication, widgets
- Recipe importer (URL/ingredients), meal planner, grocery list
- Allergens filter, cultural foods coverage, multi-language
- Offline-first with seamless sync when online

## Non-Functional Requirements
- Privacy first: local storage by default; explicit opt-in for cloud sync
- Performance: quick food search (<200 ms local DB median)
- Reliability: safe local persistence, background sync with retry
- Usability: accessible (TalkBack), color-contrast compliance, larger text options
- Security: secure storage for auth tokens; no sensitive health diagnosis stored

## Calculations (contract)
- BMR: Mifflin–St Jeor; TDEE = BMR × activity factor; goal adjustment ±10–20%
- Protein: 1.6–2.2 g/kg body weight (lower range by default)
- Fat: 25–35% of calories (default 30%)
- Carbs: remaining calories after protein + fat
- Fiber: 14 g per 1000 kcal (estimate)
- Sodium: max 2300 mg/day (general guidance)

## Data Model (local)
- UserProfile(id, sex, age, height_cm, weight_kg, activity, goal, preferences, allergies, created_at)
- DailyTargets(date, calories, protein_g, fat_g, carbs_g, fiber_g, sodium_mg)
- Food(id, name, brand, category, per_100g: macros + micros)
- Entry(id, date, meal_type, food_id, quantity_g, overrides)
- EntryNutrition(derived per entry for faster queries)

## Activity Levels
- sedentary (1.2), light (1.375), moderate (1.55), active (1.725), very_active (1.9)

## User Stories
- As a user, I can set my profile and goals so I get personal targets.
- As a user, I can search and add foods by weight so I can log accurately.
- As a user, I see remaining calories and macros throughout the day so I stay on track.
- As a user, I can edit or delete entries with correct recalculation.

## Acceptance Criteria (MVP)
- Can set profile and see computed daily targets instantly
- Can add at least 1000 foods locally and log entries offline
- Daily analytics screen shows consumed, remaining, and % to goal
- Data persists across app restarts

## Risks & Mitigations
- Food coverage gaps → allow custom foods & quick add macros
- Model inaccuracies → show confidence, allow manual correction
- Scope creep → phase the AI features; ship MVP first

## Timeline (12 weeks)
- Weeks 1–2: Design, schema, mock data, Compose UI scaffolding
- Weeks 3–4: Profile + calculations; local DB with foods and entries
- Weeks 5–6: Search, add/edit/delete entries; daily analytics
- Weeks 7–8: Nice charts, export CSV; polish and QA
- Weeks 9–10: Barcode scan (ML Kit) + simple NLP food mapping
- Weeks 11–12: Optional cloud sync, final testing, presentation deck
