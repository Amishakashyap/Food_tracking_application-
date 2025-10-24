# Android DB integration (SQLite + Room)

Goal: Ship a prebuilt on-device database of foods with per-100g nutrition and fast name search, then compute entry nutrition by scaling per-100g values by quantity.

## 1) Build the DB from CSV
Use the provided script to generate `data/food_catalog.db`:

- Windows PowerShell
```powershell
python .\scripts\build_food_db.py --csv .\cleaned_nutrition_dataset_per100g.csv --out .\data\food_catalog.db
```

## 2) Package with the app
Place the DB into your Android module assets:
- `app/src/main/assets/databases/food_catalog.db`

Then load it using Room:
```kotlin
val db = Room.databaseBuilder(appContext, AppDatabase::class.java, "food.db")
    .createFromAsset("databases/food_catalog.db")
    .fallbackToDestructiveMigration() // during development
    .build()
```

## 3) Room schema (Kotlin)
```kotlin
@Entity(tableName = "food")
data class Food(
    @PrimaryKey val id: Long,
    val name: String,
    @ColumnInfo(name = "name_normalized") val nameNormalized: String,
    @ColumnInfo(name = "calories_kcal_per_100g") val calories: Double?,
    @ColumnInfo(name = "protein_g_per_100g") val proteinG: Double?,
    @ColumnInfo(name = "fat_g_per_100g") val fatG: Double?,
    @ColumnInfo(name = "carbs_g_per_100g") val carbsG: Double?,
    @ColumnInfo(name = "fiber_g_per_100g") val fiberG: Double?,
    @ColumnInfo(name = "sugar_g_per_100g") val sugarG: Double?,
    @ColumnInfo(name = "sodium_mg_per_100g") val sodiumMg: Double?,
    @ColumnInfo(name = "calcium_mg_per_100g") val calciumMg: Double?,
    @ColumnInfo(name = "iron_mg_per_100g") val ironMg: Double?,
    @ColumnInfo(name = "vitamin_c_mg_per_100g") val vitaminCMg: Double?,
    @ColumnInfo(name = "vitamin_b11_mg_per_100g") val vitaminB11Mg: Double?,
)

@Dao
interface FoodDao {
    @Query("SELECT * FROM food WHERE id = :id")
    suspend fun getById(id: Long): Food?

    // Simple normalized search using LIKE (works without FTS)
    @Query("SELECT * FROM food WHERE name_normalized LIKE :q LIMIT 50")
    suspend fun searchLike(q: String): List<Food>

    // If you want to call the FTS table directly via rawQuery:
    @RawQuery
    suspend fun searchFts(query: SupportSQLiteQuery): List<Food>
}

@Database(entities = [Food::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}
```

FTS access example:
```kotlin
val sql = """
SELECT f.* FROM food f
JOIN food_fts ft ON f.id = ft.rowid
WHERE ft MATCH ?
ORDER BY rank
LIMIT 50
""".trimIndent()
val query = SimpleSQLiteQuery(sql, arrayOf("chicken*"))
val results = db.foodDao().searchFts(query)
```

Tip: Start with `LIKE '%term%'` search while you bring up the UI. Add FTS queries later for speed and better ranking.

## 4) Compute entry nutrition from quantity
Given per-100g values and a user-entered quantity in grams, scale as:
- calories = (calories_kcal_per_100g or 0) * quantity_g / 100
- protein_g = (protein_g_per_100g or 0) * quantity_g / 100, etc.

Store entries like:
- `Entry(id, date, meal_type, food_id, quantity_g)`
Compute totals on the fly or cache per-entry derived values for performance.

## 5) User flow
- User selects a food via search → you display per-100g macros.
- User enters quantity (g) → show computed nutrition.
- Save entry and update the daily analytics screen (consumed vs target, remaining).

## Notes
- All columns are per 100g. If some values are null in CSV, treat as 0 when computing.
- You don’t need a learning model to get nutrition values if the food is found. A model is more useful for mapping free text → correct food row. Keep MVP simple with search.
