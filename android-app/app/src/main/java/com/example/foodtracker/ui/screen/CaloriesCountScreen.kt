package com.example.foodtracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.foodtracker.data.db.AppDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CaloriesCountScreen(padding: PaddingValues = PaddingValues(0.dp)) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val scope = rememberCoroutineScope()
    
    var totalCalories by remember { mutableStateOf(0.0) }
    var targetCalories by remember { mutableStateOf(2000) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val entries = db.entryDao().getByDate(today)
                
                var sum = 0.0
                entries.forEach { entry ->
                    val food = db.foodDao().getById(entry.foodId)
                    if (food != null) {
                        val factor = entry.quantityG / 100.0
                        sum += (food.calories ?: 0.0) * factor
                    }
                }
                totalCalories = sum
                
                // TODO: load actual target from user profile
                // For now, use default 2000
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }
    
    if (isLoading) {
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    val remaining = (targetCalories - totalCalories).coerceAtLeast(0.0)
    val progress = (totalCalories / targetCalories).toFloat().coerceIn(0f, 1f)

    Column(
        Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calories Count", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Today's Calories", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                
                Text("%.0f".format(totalCalories), style = MaterialTheme.typography.displayLarge)
                Text("out of $targetCalories kcal", style = MaterialTheme.typography.bodyLarge)
                
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(16.dp)
                )
                
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Consumed", style = MaterialTheme.typography.labelLarge)
                        Text("%.0f kcal".format(totalCalories), style = MaterialTheme.typography.titleMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Remaining", style = MaterialTheme.typography.labelLarge)
                        Text("%.0f kcal".format(remaining), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        if (totalCalories > targetCalories) {
            Text("⚠️ You've exceeded your daily calorie target", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        } else if (remaining < 200) {
            Text("✓ Almost there! Only %.0f kcal remaining".format(remaining), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyLarge)
        } else {
            Text("Keep going! You have %.0f kcal left for today".format(remaining), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
