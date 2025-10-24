#!/usr/bin/env python3
"""
Build an on-device SQLite food catalog from the provided CSV.
- Input: cleaned_nutrition_dataset_per100g.csv (UTF-8 CSV with per-100g fields)
- Output: data/food_catalog.db (SQLite with Food table + FTS5 index)

Usage (PowerShell):
  python .\scripts\build_food_db.py --csv .\cleaned_nutrition_dataset_per100g.csv --out .\data\food_catalog.db

This DB can be shipped inside the Android app assets and loaded with Room using
createFromAsset("databases/food_catalog.db").
"""
from __future__ import annotations
import argparse
import csv
import os
import sqlite3
from pathlib import Path

# All Room tables - must match the Entity definitions exactly
USER_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS user (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  email TEXT NOT NULL,
  password TEXT NOT NULL,
  name TEXT NOT NULL,
  city TEXT NOT NULL,
  gender TEXT NOT NULL,
  age INTEGER NOT NULL,
  blood_group TEXT NOT NULL
);
"""

FOOD_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS food (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  name TEXT NOT NULL,
  name_normalized TEXT NOT NULL,
  calories_kcal_per_100g REAL,
  protein_g_per_100g REAL,
  fat_g_per_100g REAL,
  carbs_g_per_100g REAL,
  fiber_g_per_100g REAL,
  sugar_g_per_100g REAL,
  sodium_mg_per_100g REAL,
  calcium_mg_per_100g REAL,
  iron_mg_per_100g REAL,
  vitamin_c_mg_per_100g REAL,
  vitamin_b11_mg_per_100g REAL
);
"""

ENTRY_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS entry (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  date TEXT NOT NULL,
  meal_type TEXT NOT NULL,
  food_id INTEGER NOT NULL,
  quantity_g REAL NOT NULL
);
"""

USER_PROFILE_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS user_profile (
  userId INTEGER PRIMARY KEY NOT NULL,
  medical_history TEXT NOT NULL,
  goal TEXT NOT NULL,
  weight_kg REAL NOT NULL,
  height_cm REAL NOT NULL,
  target_weight_kg REAL NOT NULL,
  exercise_freq TEXT NOT NULL
);
"""

# Contentless FTS tied to food via content=food; we index by name and name_normalized.
FOOD_FTS_SQL = """
CREATE VIRTUAL TABLE IF NOT EXISTS food_fts USING fts5(
  name, name_normalized, content='food', content_rowid='id'
);
"""

TRIGGERS_SQL = """
CREATE TRIGGER IF NOT EXISTS food_ai AFTER INSERT ON food BEGIN
  INSERT INTO food_fts(rowid, name, name_normalized)
  VALUES (new.id, new.name, new.name_normalized);
END;
CREATE TRIGGER IF NOT EXISTS food_ad AFTER DELETE ON food BEGIN
  INSERT INTO food_fts(food_fts, rowid, name, name_normalized)
  VALUES ('delete', old.id, old.name, old.name_normalized);
END;
CREATE TRIGGER IF NOT EXISTS food_au AFTER UPDATE ON food BEGIN
  INSERT INTO food_fts(food_fts, rowid, name, name_normalized)
  VALUES ('delete', old.id, old.name, old.name_normalized);
  INSERT INTO food_fts(rowid, name, name_normalized)
  VALUES (new.id, new.name, new.name_normalized);
END;
"""

INDEXES_SQL = """
CREATE INDEX IF NOT EXISTS idx_food_name_norm ON food(name_normalized);
"""

EXPECTED_COLUMNS = {
    "Vitamin C (mg per 100g)": "vitamin_c_mg_per_100g",
    "Vitamin B11 (mg per 100g)": "vitamin_b11_mg_per_100g",
    "Sodium (mg per 100g)": "sodium_mg_per_100g",
    "Calcium (mg per 100g)": "calcium_mg_per_100g",
    "Carbohydrates (g per 100g)": "carbs_g_per_100g",
    "food": "name",
    "Iron (mg per 100g)": "iron_mg_per_100g",
    "Calories (kcal per 100g)": "calories_kcal_per_100g",
    "Sugars (g per 100g)": "sugar_g_per_100g",
    "Dietary Fiber (g per 100g)": "fiber_g_per_100g",
    "Fat (g per 100g)": "fat_g_per_100g",
    "Protein (g per 100g)": "protein_g_per_100g",
    "food_normalized": "name_normalized",
}


def norm_name(s: str) -> str:
    return (s or "").strip().lower()


def build_db(csv_path: Path, out_path: Path) -> None:
  out_path.parent.mkdir(parents=True, exist_ok=True)
  if out_path.exists():
    out_path.unlink()

  conn = sqlite3.connect(out_path)
  try:
    conn.execute("PRAGMA journal_mode=WAL;")
    conn.execute("PRAGMA synchronous=NORMAL;")
    # Create ALL Room tables (user/entry/profile empty, food will be populated)
    conn.executescript(USER_TABLE_SQL)
    conn.executescript(FOOD_TABLE_SQL)
    conn.executescript(ENTRY_TABLE_SQL)
    conn.executescript(USER_PROFILE_TABLE_SQL)
    conn.executescript(FOOD_FTS_SQL)
    conn.executescript(TRIGGERS_SQL)
    conn.executescript(INDEXES_SQL)

    with open(csv_path, newline='', encoding='utf-8') as f:
      reader = csv.DictReader(f)
      missing = [c for c in EXPECTED_COLUMNS.keys() if c not in reader.fieldnames]
      if missing:
        raise ValueError(f"CSV missing expected columns: {missing}")

      rows = []
      for row in reader:
        name = row.get("food") or row.get("food_normalized") or ""
        name_n = row.get("food_normalized") or norm_name(name)

        def parse_float(key: str) -> float | None:
          val = row.get(key, "").strip()
          if val == "" or val.lower() == "na":
            return None
          try:
            return float(val)
          except ValueError:
            return None

        mapped = {
          "name": name,
          "name_normalized": norm_name(name_n),
          "calories_kcal_per_100g": parse_float("Calories (kcal per 100g)"),
          "protein_g_per_100g": parse_float("Protein (g per 100g)"),
          "fat_g_per_100g": parse_float("Fat (g per 100g)"),
          "carbs_g_per_100g": parse_float("Carbohydrates (g per 100g)"),
          "fiber_g_per_100g": parse_float("Dietary Fiber (g per 100g)"),
          "sugar_g_per_100g": parse_float("Sugars (g per 100g)"),
          "sodium_mg_per_100g": parse_float("Sodium (mg per 100g)"),
          "calcium_mg_per_100g": parse_float("Calcium (mg per 100g)"),
          "iron_mg_per_100g": parse_float("Iron (mg per 100g)"),
          "vitamin_c_mg_per_100g": parse_float("Vitamin C (mg per 100g)"),
          "vitamin_b11_mg_per_100g": parse_float("Vitamin B11 (mg per 100g)"),
        }
        rows.append(mapped)

    with conn:
      conn.executemany(
        """
        INSERT INTO food(
          name, name_normalized,
          calories_kcal_per_100g, protein_g_per_100g, fat_g_per_100g, carbs_g_per_100g,
          fiber_g_per_100g, sugar_g_per_100g, sodium_mg_per_100g, calcium_mg_per_100g,
          iron_mg_per_100g, vitamin_c_mg_per_100g, vitamin_b11_mg_per_100g
        ) VALUES (
          :name, :name_normalized,
          :calories_kcal_per_100g, :protein_g_per_100g, :fat_g_per_100g, :carbs_g_per_100g,
          :fiber_g_per_100g, :sugar_g_per_100g, :sodium_mg_per_100g, :calcium_mg_per_100g,
          :iron_mg_per_100g, :vitamin_c_mg_per_100g, :vitamin_b11_mg_per_100g
        )
        """,
        rows,
      )

    # Optimize
    conn.execute("VACUUM;")
    conn.execute("ANALYZE;")

    # Quick stats
    cur = conn.execute("SELECT COUNT(*) FROM food")
    count = cur.fetchone()[0]
    print(f"Built DB at {out_path} with {count} foods.")

  finally:
    conn.close()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--csv", required=True, help="Path to cleaned_nutrition_dataset_per100g.csv")
    ap.add_argument("--out", default=str(Path("data") / "food_catalog.db"), help="Output SQLite path")
    args = ap.parse_args()

    csv_path = Path(args.csv)
    out_path = Path(args.out)
    build_db(csv_path, out_path)


if __name__ == "__main__":
    main()
