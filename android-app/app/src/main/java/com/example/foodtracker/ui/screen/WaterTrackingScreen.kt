package com.example.foodtracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WaterTrackingScreen(padding: PaddingValues = PaddingValues(0.dp)) {
    var glasses by remember { mutableStateOf(0) }
    val target = 8 // 8 glasses per day

    Column(
        Modifier
            .padding(padding)
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Water Tracking", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Daily Water Intake", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Text("$glasses / $target glasses", style = MaterialTheme.typography.displayMedium)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (glasses.toFloat() / target).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(12.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("${(glasses * 250)} ml consumed", style = MaterialTheme.typography.bodyLarge)
                Text("Target: ${target * 250} ml (2 liters)", style = MaterialTheme.typography.bodyMedium)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { if (glasses > 0) glasses-- },
                modifier = Modifier.weight(1f)
            ) {
                Text("-")
            }
            Button(
                onClick = { glasses++ },
                modifier = Modifier.weight(1f)
            ) {
                Text("+")
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        Button(
            onClick = { glasses = 0 },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }
        
        Spacer(Modifier.height(24.dp))
        
        Text("Tip: Aim for 8 glasses (250ml each) of water per day.", style = MaterialTheme.typography.bodyMedium)
    }
}
