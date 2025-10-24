#!/usr/bin/env python3
"""
Build an on-device SQLite food catalog from the provided CSV.
ONLY creates the food table and FTS index - NO other tables!
"""
from __future__ import annotations
import argparse
import csv
import sqlite3
from pathlib import Path

def build_db(csv_path: Path, out_path: Path) -> None:
    """Build database with ONLY food table and FTS index."""
    out_path.parent.mkdir(parents=True, exist_ok=True)
    if out_path.exists():
        out_path.unlink()

    conn = sqlite3.connect(out_path)
    try:
        # Create food table
        conn.execute("""
            CREATE TABLE food (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
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
            )
        """)
        
        # Create FTS table
        conn.execute("""
            CREATE VIRTUAL TABLE food_fts USING fts5(
              name, name_normalized, content='food', content_rowid='id'
            )
        """)
        
        # Create triggers for FTS
        conn.execute("""
            CREATE TRIGGER food_ai AFTER INSERT ON food BEGIN
              INSERT INTO food_fts(rowid, name, name_normalized)
              VALUES (new.id, new.name, new.name_normalized);
            END
        """)
        conn.execute("""
            CREATE TRIGGER food_ad AFTER DELETE ON food BEGIN
              INSERT INTO food_fts(food_fts, rowid, name, name_normalized)
              VALUES ('delete', old.id, old.name, old.name_normalized);
            END
        """)
        conn.execute("""
            CREATE TRIGGER food_au AFTER UPDATE ON food BEGIN
              INSERT INTO food_fts(food_fts, rowid, name, name_normalized)
              VALUES ('delete', old.id, old.name, old.name_normalized);
              INSERT INTO food_fts(rowid, name, name_normalized)
              VALUES (new.id, new.name, new.name_normalized);
            END
        """)
        
        # Create index
        conn.execute("CREATE INDEX idx_food_name_norm ON food(name_normalized)")
        
        # Read CSV and insert data
        with open(csv_path, newline='', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            rows = []
            for row in reader:
                name = row.get("food", "").strip()
                name_n = row.get("food_normalized", name).strip().lower()
                
                def parse_float(key: str) -> float | None:
                    val = row.get(key, "").strip()
                    if val == "" or val.lower() == "na":
                        return None
                    try:
                        return float(val)
                    except ValueError:
                        return None
                
                rows.append({
                    "name": name,
                    "name_normalized": name_n,
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
                })
        
        conn.executemany("""
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
        """, rows)
        
        conn.commit()
        conn.execute("VACUUM")
        conn.execute("ANALYZE")
        
        count = conn.execute("SELECT COUNT(*) FROM food").fetchone()[0]
        tables = [r[0] for r in conn.execute("SELECT name FROM sqlite_master WHERE type='table'")]
        print(f"Built DB at {out_path} with {count} foods.")
        print(f"Tables: {tables}")
        
    finally:
        conn.close()

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--csv", required=True, help="Path to cleaned_nutrition_dataset_per100g.csv")
    ap.add_argument("--out", default="data/food_catalog.db", help="Output SQLite path")
    args = ap.parse_args()
    
    csv_path = Path(args.csv)
    out_path = Path(args.out)
    build_db(csv_path, out_path)

if __name__ == "__main__":
    main()
