package com.example.foodtracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DietPlanScreen(padding: PaddingValues = PaddingValues(0.dp)) {
    Column(
        Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Diet Plan", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        Text("Personalized meal plans based on your goals", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Sample Daily Plan", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                
                Text("Breakfast (7:00 AM)", style = MaterialTheme.typography.titleMedium)
                Text("• Oatmeal with banana and almonds")
                Text("• Green tea")
                Spacer(Modifier.height(8.dp))
                
                Text("Mid-Morning Snack (10:00 AM)", style = MaterialTheme.typography.titleMedium)
                Text("• Greek yogurt with berries")
                Spacer(Modifier.height(8.dp))
                
                Text("Lunch (1:00 PM)", style = MaterialTheme.typography.titleMedium)
                Text("• Grilled chicken breast")
                Text("• Brown rice")
                Text("• Mixed vegetables")
                Spacer(Modifier.height(8.dp))
                
                Text("Evening Snack (4:00 PM)", style = MaterialTheme.typography.titleMedium)
                Text("• Apple with peanut butter")
                Spacer(Modifier.height(8.dp))
                
                Text("Dinner (7:00 PM)", style = MaterialTheme.typography.titleMedium)
                Text("• Baked salmon")
                Text("• Quinoa")
                Text("• Steamed broccoli")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Text("Feature coming soon: AI-generated meal plans based on your preferences, allergies, and nutritional goals.", style = MaterialTheme.typography.bodyMedium)
    }
}
