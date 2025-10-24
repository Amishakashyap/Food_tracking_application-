package com.example.foodtracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodtracker.ui.screen.*

sealed class Dest(val route: String, val label: String = "") {
    data object Login : Dest("login")
    data object Register : Dest("register")
    data object ProfileSetup : Dest("profileSetup")
    data object Dashboard : Dest("dashboard", "Dashboard")
    data object AddFood : Dest("addFood", "Add Food")
    data object Analytics : Dest("analytics", "Analytics")
    data object Summary : Dest("summary", "Summary")
    data object BMI : Dest("bmi")
    data object Calories : Dest("calories")
    data object Water : Dest("water")
    data object Muscle : Dest("muscle")
    data object DietPlan : Dest("dietPlan")
    data object News : Dest("news")
}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    var currentUserId by remember { mutableStateOf<Long?>(null) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = remember(navBackStackEntry) {
        val route = navBackStackEntry?.destination?.route
        route in listOf(Dest.Dashboard.route, Dest.AddFood.route, Dest.Analytics.route, Dest.Summary.route)
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Login.route
        ) {
            composable(Dest.Login.route) {
                LoginScreen(
                    onLoginSuccess = { userId ->
                        currentUserId = userId
                        navController.navigate(Dest.Dashboard.route) {
                            popUpTo(Dest.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Dest.Register.route) }
                )
            }
            composable(Dest.Register.route) {
                RegistrationScreen(
                    onRegisterSuccess = { userId ->
                        currentUserId = userId
                        navController.navigate(Dest.ProfileSetup.route) {
                            popUpTo(Dest.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Dest.ProfileSetup.route) {
                ProfileSetupScreen(
                    userId = currentUserId ?: 1L,
                    onComplete = {
                        navController.navigate(Dest.Dashboard.route) {
                            popUpTo(Dest.ProfileSetup.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Dest.Dashboard.route) {
                DashboardScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    padding = padding
                )
            }
            composable(Dest.AddFood.route) { AddFoodScreen(padding, currentUserId ?: 1L) }
            composable(Dest.Analytics.route) { AnalyticsScreen(padding) }
            composable(Dest.Summary.route) { SummaryScreen(padding) }
            composable(Dest.BMI.route) { BMICalculatorScreen(padding) }
            composable(Dest.Calories.route) { CaloriesCountScreen(padding) }
            composable(Dest.Water.route) { WaterTrackingScreen(padding) }
            composable(Dest.Muscle.route) { MuscleGainScreen(padding) }
            composable(Dest.DietPlan.route) { DietPlanScreen(padding) }
            composable(Dest.News.route) { NewsScreen(padding) }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val items = listOf(Dest.Dashboard, Dest.AddFood, Dest.Analytics, Dest.Summary)
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { dest ->
            NavigationBarItem(
                selected = currentRoute == dest.route,
                onClick = {
                    navController.navigate(dest.route) {
                        popUpTo(Dest.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { /* icons optional */ },
                label = { Text(dest.label) }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, padding: PaddingValues) {
    Column(
        Modifier.padding(padding).padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("This feature is under development.")
    }
}
