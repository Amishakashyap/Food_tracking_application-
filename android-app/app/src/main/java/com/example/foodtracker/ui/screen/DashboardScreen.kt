package com.example.foodtracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class DashboardCard(val label: String, val route: String)

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    val cards = listOf(
        DashboardCard("BMI Calculator", "bmi"),
        DashboardCard("Food Analysis", "analytics"),
        DashboardCard("Add Food", "addFood"),
        DashboardCard("Calories Count", "calories"),
        DashboardCard("Water Tracking", "water"),
        DashboardCard("Muscle Gain", "muscle"),
        DashboardCard("Diet Plan", "dietPlan"),
        DashboardCard("News", "news"),
    )

    Column(Modifier.padding(padding).padding(16.dp)) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .clickable { onNavigate(card.route) },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(card.label, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
