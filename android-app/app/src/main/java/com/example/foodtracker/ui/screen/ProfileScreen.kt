package com.example.foodtracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(padding: PaddingValues) {
    Column(Modifier.padding(padding).padding(16.dp)) {
        Text("Profile", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Text("View and edit your profile here.")
        // TODO: load User and UserProfile, allow editing
    }
}
