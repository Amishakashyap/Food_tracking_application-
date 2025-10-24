package com.example.foodtracker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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

@Entity(tableName = "entry")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // yyyy-MM-dd
    @ColumnInfo(name = "meal_type") val mealType: String, // breakfast|lunch|dinner|snack
    @ColumnInfo(name = "food_id") val foodId: Long,
    @ColumnInfo(name = "quantity_g") val quantityG: Double,
)

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val password: String, // Note: hash in production
    val name: String,
    val city: String,
    val gender: String,
    val age: Int,
    @ColumnInfo(name = "blood_group") val bloodGroup: String,
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val userId: Long,
    @ColumnInfo(name = "medical_history") val medicalHistory: String,
    val goal: String, // weightloss|gain|muscle
    @ColumnInfo(name = "weight_kg") val weightKg: Double,
    @ColumnInfo(name = "height_cm") val heightCm: Double,
    @ColumnInfo(name = "target_weight_kg") val targetWeightKg: Double,
    @ColumnInfo(name = "exercise_freq") val exerciseFreq: String, // none|twice_week|regular|proper_workout
)
