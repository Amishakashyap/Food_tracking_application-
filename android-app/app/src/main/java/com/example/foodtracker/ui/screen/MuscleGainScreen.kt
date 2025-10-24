package com.example.foodtracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MuscleGainScreen(padding: PaddingValues = PaddingValues(0.dp)) {
    Column(
        Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Muscle Gain Tracker", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Progress Overview", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Track your muscle gain journey with:")
                Spacer(Modifier.height(8.dp))
                Text("• Daily protein intake target")
                Text("• Strength training log")
                Text("• Body measurements")
                Text("• Progress photos")
                Text("• Workout plans")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Recommended Protein", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("For muscle gain: 2.0-2.2g per kg body weight")
                Spacer(Modifier.height(4.dp))
                Text("Example: 70kg person → 140-154g protein/day")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Text("Feature coming soon: detailed workout tracking, muscle group analysis, and personalized recommendations.", style = MaterialTheme.typography.bodyMedium)
    }
}
