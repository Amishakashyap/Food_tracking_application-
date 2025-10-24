package com.example.foodtracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class NewsItem(val title: String, val summary: String, val source: String)

@Composable
fun NewsScreen(padding: PaddingValues = PaddingValues(0.dp)) {
    val newsItems = listOf(
        NewsItem(
            "10 Superfoods for Better Health",
            "Discover nutrient-rich foods that can boost your immune system and overall wellness.",
            "Health Today"
        ),
        NewsItem(
            "The Science of Protein Timing",
            "New research shows when you eat protein matters as much as how much you eat.",
            "Nutrition Journal"
        ),
        NewsItem(
            "Hydration: More Than Just Water",
            "Learn about electrolytes and optimal hydration strategies for active lifestyles.",
            "Fitness Weekly"
        ),
        NewsItem(
            "Plant-Based Diets: What You Need to Know",
            "A comprehensive guide to transitioning to plant-based eating while meeting nutritional needs.",
            "Diet Science"
        ),
        NewsItem(
            "Sleep and Nutrition: The Hidden Connection",
            "How your diet affects sleep quality and vice versa.",
            "Wellness Magazine"
        )
    )

    Column(
        Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Health & Nutrition News", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        newsItems.forEach { item ->
            NewsCard(item)
            Spacer(Modifier.height(12.dp))
        }
        
        Spacer(Modifier.height(8.dp))
        Text("Feature coming soon: Live news feed from trusted health and nutrition sources.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun NewsCard(item: NewsItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: open full article */ },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(item.summary, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(item.source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
