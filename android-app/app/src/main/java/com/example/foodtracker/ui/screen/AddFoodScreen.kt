package com.example.foodtracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.foodtracker.data.db.AppDatabase
import com.example.foodtracker.data.db.Entry
import com.example.foodtracker.data.db.Food
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddFoodScreen(padding: PaddingValues, @Suppress("UNUSED_PARAMETER") userId: Long) {
    val context = LocalContext.current
    val db = AppDatabase.get(context)
    val scope = rememberCoroutineScope()
    
    var query by remember { mutableStateOf("") }
    var grams by remember { mutableStateOf("100") }
    var searchResults by remember { mutableStateOf<List<Food>>(emptyList()) }
    var selectedMeal by remember { mutableStateOf("breakfast") }
    var message by remember { mutableStateOf("") }
    
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val today = remember { dateFormat.format(Date()) }
    
    Column(Modifier.padding(padding).padding(16.dp)) {
        Text("Add Food Entry", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search food (English)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            value = grams,
            onValueChange = { grams = it },
            label = { Text("Quantity (grams)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        
        // Meal type selector
        Text("Meal Type:", style = MaterialTheme.typography.labelMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("breakfast", "lunch", "dinner", "snack").forEach { meal ->
                FilterChip(
                    selected = selectedMeal == meal,
                    onClick = { selectedMeal = meal },
                    label = { Text(meal.capitalize()) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        
        Button(
            onClick = {
                scope.launch {
                    try {
                        val results = db.foodDao().searchLike("%${query.trim()}%")
                        searchResults = results
                        message = if (results.isEmpty()) "No foods found" else "${results.size} results"
                    } catch (e: Exception) {
                        message = "Search error: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }
        
        if (message.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
        
        Spacer(Modifier.height(12.dp))
        Text("Tap a food to add:", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(searchResults) { food ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                try {
                                    val quantity = grams.toDoubleOrNull() ?: 100.0
                                    val entry = Entry(
                                        date = today,
                                        mealType = selectedMeal,
                                        foodId = food.id,
                                        quantityG = quantity
                                    )
                                    db.entryDao().upsert(entry)
                                    message = "Added ${food.name} (${quantity}g) to $selectedMeal"
                                    searchResults = emptyList()
                                    query = ""
                                } catch (e: Exception) {
                                    message = "Error adding: ${e.message}"
                                }
                            }
                        }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(food.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Per 100g: ${food.calories?.toInt() ?: 0} cal | " +
                            "P: ${food.proteinG?.toInt() ?: 0}g | " +
                            "F: ${food.fatG?.toInt() ?: 0}g | " +
                            "C: ${food.carbsG?.toInt() ?: 0}g",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

private fun String.capitalize() = replaceFirstChar { it.uppercase() }
