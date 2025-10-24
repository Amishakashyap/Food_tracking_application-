package com.example.foodtracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.foodtracker.data.db.AppDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MealSummary(
    val mealType: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val fiber: Double,
    val sodium: Double
)

@Composable
fun AnalyticsScreen(
    padding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val scope = rememberCoroutineScope()
    
    var meals by remember { mutableStateOf<List<MealSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Load data on composition
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val entries = db.entryDao().getByDate(today)
                
                // Group by meal type and compute sums
                val grouped = entries.groupBy { it.mealType }
                val mealSummaries = mutableListOf<MealSummary>()
                
                listOf("breakfast", "lunch", "dinner", "snack").forEach { mealType ->
                    val mealEntries = grouped[mealType] ?: emptyList()
                    var totalCal = 0.0
                    var totalProt = 0.0
                    var totalFat = 0.0
                    var totalCarbs = 0.0
                    var totalFiber = 0.0
                    var totalSodium = 0.0
                    
                    mealEntries.forEach { entry ->
                        val food = db.foodDao().getById(entry.foodId)
                        if (food != null) {
                            val factor = entry.quantityG / 100.0
                            totalCal += (food.calories ?: 0.0) * factor
                            totalProt += (food.proteinG ?: 0.0) * factor
                            totalFat += (food.fatG ?: 0.0) * factor
                            totalCarbs += (food.carbsG ?: 0.0) * factor
                            totalFiber += (food.fiberG ?: 0.0) * factor
                            totalSodium += (food.sodiumMg ?: 0.0) * factor
                        }
                    }
                    
                    if (mealEntries.isNotEmpty()) {
                        mealSummaries.add(
                            MealSummary(
                                mealType.replaceFirstChar { it.uppercase() },
                                totalCal, totalProt, totalFat, totalCarbs, totalFiber, totalSodium
                            )
                        )
                    }
                }
                
                meals = mealSummaries
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }
    
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    val total = if (meals.isNotEmpty()) {
        MealSummary(
            "Total",
            meals.sumOf { it.calories },
            meals.sumOf { it.protein },
            meals.sumOf { it.fat },
            meals.sumOf { it.carbs },
            meals.sumOf { it.fiber },
            meals.sumOf { it.sodium }
        )
    } else {
        MealSummary("Total", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    Column(
        Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Analytics - Today's Macronutrients", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        if (meals.isEmpty()) {
            Text("No food entries for today yet. Add some meals to see your analytics!", style = MaterialTheme.typography.bodyLarge)
        } else {
            meals.forEach { meal ->
                MealCard(meal)
                Spacer(Modifier.height(12.dp))
            }
            
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            
            Text("Daily Total", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            MacroRow("Calories", total.calories, "kcal")
            MacroRow("Protein", total.protein, "g")
            MacroRow("Fat", total.fat, "g")
            MacroRow("Carbs", total.carbs, "g")
            MacroRow("Fiber", total.fiber, "g")
            MacroRow("Sodium", total.sodium, "mg")
        }
    }
}

@Composable
private fun MealCard(meal: MealSummary) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(meal.mealType, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            MacroRow("Calories", meal.calories, "kcal")
            MacroRow("Protein", meal.protein, "g")
            MacroRow("Fat", meal.fat, "g")
            MacroRow("Carbs", meal.carbs, "g")
            MacroRow("Fiber", meal.fiber, "g")
            MacroRow("Sodium", meal.sodium, "mg")
        }
    }
}

@Composable
private fun MacroRow(label: String, value: Double, unit: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text("%.1f %s".format(value, unit))
    }
}
