package com.example.foodtracker.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SummaryScreen(padding: PaddingValues) {
    Column(Modifier.padding(padding).padding(16.dp)) {
        Text("Daily Summary", style = MaterialTheme.typography.titleLarge)
        // TODO: consumed vs target and remaining for: Calories, Protein, Fat, Carbs, Fiber, Sodium
        Text("Calories: consumed / target (remaining)")
        Text("Protein: consumed / target (remaining)")
        Text("Fat: consumed / target (remaining)")
        Text("Carbs: consumed / target (remaining)")
        Text("Fiber: consumed / target (remaining)")
        Text("Sodium: consumed / target (remaining)")
    }
}
