# Nutrition Calculations

This doc defines the formulas and defaults used to compute daily targets from a user profile.

## Inputs
- sex: male|female (for BMR)
- age: years
- height_cm
- weight_kg
- activity: sedentary|light|moderate|active|very_active
- goal: lose|maintain|gain
- optional: body_fat_pct (if available, we can compute Katch–McArdle BMR)

## BMR
- Mifflin–St Jeor:
  - Male: BMR = 10*weight_kg + 6.25*height_cm − 5*age + 5
  - Female: BMR = 10*weight_kg + 6.25*height_cm − 5*age − 161
- Optional Katch–McArdle if body fat % known:
  - LBM = weight_kg * (1 − body_fat_pct/100)
  - BMR = 370 + 21.6 * LBM

## TDEE
- Activity factor:
  - sedentary: 1.2
  - light: 1.375
  - moderate: 1.55
  - active: 1.725
  - very_active: 1.9
- TDEE = BMR * activity_factor

## Goal Adjustment
- lose: TDEE − 15% (cap at −20%)
- maintain: TDEE
- gain: TDEE + 10% (cap at +20%)

## Macro Targets
- Protein: default 1.8 g/kg (range 1.6–2.2); for weight loss use 2.0 g/kg
- Fat: default 30% of calories (range 25–35%)
- Carbs: remaining calories after protein and fat

Conversion: 1 g protein = 4 kcal; 1 g carbs = 4 kcal; 1 g fat = 9 kcal.

Example: weight 70 kg → protein 1.8*70 = 126 g → 504 kcal.
If calories = 2400 kcal and fat 30% = 720 kcal → fat = 80 g.
Carbs = 2400 − (504+720) = 1176 kcal → 294 g carbs.

## Micronutrient Guidance
- Fiber: 14 g per 1000 kcal (e.g., 2400 kcal → ~34 g fiber)
- Sodium: general guideline ≤ 2300 mg/day
- Sugar: track added sugars if available in dataset (optional goal)

## Edge Cases & Safeguards
- Minimum calories clamp: not below 1200 kcal (female) / 1500 kcal (male) unless explicitly overridden
- Protein minimum: at least 1.2 g/kg even in low-calorie plans
- Fat minimum: at least 0.6 g/kg to maintain hormonal function
- Round targets to sensible increments (e.g., nearest 5 kcal / 1 g)

## Meal Allocation (optional)
- Distribute daily macros per meal (e.g., 25% breakfast, 35% lunch, 30% dinner, 10% snacks) for guidance; not enforced.
