#!/usr/bin/env python3
"""
Quick target calculator for the Food Tracking app.
No external dependencies. Python 3.9+.

Usage example:
    python scripts/calc_targets.py --sex male --age 22 --height_cm 175 --weight_kg 70 \
        --activity moderate --goal maintain
"""
from __future__ import annotations
import argparse
import json
from dataclasses import dataclass, asdict


ACTIVITY_FACTORS = {
    "sedentary": 1.2,
    "light": 1.375,
    "moderate": 1.55,
    "active": 1.725,
    "very_active": 1.9,
}


@dataclass
class Profile:
    sex: str  # "male"|"female"
    age: int
    height_cm: float
    weight_kg: float
    activity: str  # one of ACTIVITY_FACTORS
    goal: str  # "lose"|"maintain"|"gain"
    body_fat_pct: float | None = None


def clamp_calories(sex: str, calories: float) -> float:
    min_cals = 1500 if sex.lower() == "male" else 1200
    return max(calories, min_cals)


def bmr_msj(profile: Profile) -> float:
    if profile.sex.lower() == "male":
        return 10 * profile.weight_kg + 6.25 * profile.height_cm - 5 * profile.age + 5
    else:
        return 10 * profile.weight_kg + 6.25 * profile.height_cm - 5 * profile.age - 161


def bmr_kma(profile: Profile) -> float:
    assert profile.body_fat_pct is not None
    lbm = profile.weight_kg * (1 - profile.body_fat_pct / 100)
    return 370 + 21.6 * lbm


def tdee(profile: Profile, bmr: float) -> float:
    af = ACTIVITY_FACTORS.get(profile.activity.lower())
    if af is None:
        raise ValueError(f"Unknown activity: {profile.activity}")
    return bmr * af


def adjust_for_goal(calories: float, goal: str, sex: str) -> float:
    goal_l = goal.lower()
    if goal_l == "lose":
        calories *= 0.85  # -15%
    elif goal_l == "gain":
        calories *= 1.10  # +10%
    return clamp_calories(sex, calories)


def macro_targets(profile: Profile, calories: float) -> dict:
    # Protein default 1.8 g/kg; if goal is loss, use 2.0 g/kg
    protein_per_kg = 2.0 if profile.goal.lower() == "lose" else 1.8
    protein_g = max(1.2 * profile.weight_kg, protein_per_kg * profile.weight_kg)
    protein_kcal = protein_g * 4

    # Fat default 30% of calories, but ensure >= 0.6 g/kg
    fat_kcal = calories * 0.30
    fat_g = max(0.6 * profile.weight_kg, fat_kcal / 9)
    fat_kcal = fat_g * 9  # recompute if minimum raised

    # Carbs are remaining
    carbs_kcal = max(0.0, calories - (protein_kcal + fat_kcal))
    carbs_g = carbs_kcal / 4

    # Fiber and sodium guidance
    fiber_g = round((calories / 1000.0) * 14)
    sodium_mg = 2300

    return {
        "calories": round(calories),
        "protein_g": round(protein_g),
        "fat_g": round(fat_g),
        "carbs_g": round(carbs_g),
        "fiber_g": fiber_g,
        "sodium_mg": sodium_mg,
    }


def compute_targets(profile: Profile) -> dict:
    if profile.body_fat_pct is not None:
        bmr = bmr_kma(profile)
    else:
        bmr = bmr_msj(profile)
    total = tdee(profile, bmr)
    adj = adjust_for_goal(total, profile.goal, profile.sex)
    return macro_targets(profile, adj)


def main():
    p = argparse.ArgumentParser()
    p.add_argument("--sex", required=True, choices=["male", "female"])  # simplified
    p.add_argument("--age", type=int, required=True)
    p.add_argument("--height_cm", type=float, required=True)
    p.add_argument("--weight_kg", type=float, required=True)
    p.add_argument("--activity", required=True, choices=list(ACTIVITY_FACTORS.keys()))
    p.add_argument("--goal", required=True, choices=["lose", "maintain", "gain"])
    p.add_argument("--body_fat_pct", type=float)
    args = p.parse_args()

    profile = Profile(
        sex=args.sex,
        age=args.age,
        height_cm=args.height_cm,
        weight_kg=args.weight_kg,
        activity=args.activity,
        goal=args.goal,
        body_fat_pct=args.body_fat_pct,
    )
    targets = compute_targets(profile)
    print(json.dumps({"profile": asdict(profile), "targets": targets}, indent=2))


if __name__ == "__main__":
    main()
