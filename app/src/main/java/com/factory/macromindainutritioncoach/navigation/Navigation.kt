package com.factory.macromindainutritioncoach.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.ui.screens.AddFoodScreen
import com.factory.macromindainutritioncoach.ui.screens.CoachScreen
import com.factory.macromindainutritioncoach.ui.screens.DashboardScreen
import com.factory.macromindainutritioncoach.ui.screens.FoodLogScreen
import com.factory.macromindainutritioncoach.ui.screens.PaywallScreen
import com.factory.macromindainutritioncoach.ui.screens.ProfileScreen
import com.factory.macromindainutritioncoach.ui.viewmodel.CoachViewModel
import com.factory.macromindainutritioncoach.ui.viewmodel.DashboardViewModel
import com.factory.macromindainutritioncoach.ui.viewmodel.FoodLogViewModel
import com.factory.macromindainutritioncoach.ui.viewmodel.ProfileViewModel

sealed class Screen(val route: String) {
    object Paywall : Screen("paywall")
}

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("dashboard", "Dashboard", Icons.Default.Dashboard)
    object FoodLog : BottomNavItem("food_log", "Log", Icons.Default.Restaurant)
    object AddFood : BottomNavItem("add_food", "Add", Icons.Default.Add)
    object Coach : BottomNavItem("coach", "Coach", Icons.Default.Psychology)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.FoodLog,
    BottomNavItem.AddFood,
    BottomNavItem.Coach,
    BottomNavItem.Profile
)

// Routes that should hide the bottom navigation bar
private val routesWithoutBottomBar = setOf(Screen.Paywall.route)

@Composable
fun MacroMindNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val showBottomBar = currentRoute !in routesWithoutBottomBar

    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            MacroMindNavHost(navController = navController)
        }
    }
}

@Composable
fun MacroMindNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val app = context.applicationContext as MacroMindApplication
    val isPremium by app.premiumManager.isPremium.collectAsStateWithLifecycle()

    val dashboardViewModel: DashboardViewModel = viewModel()
    val foodLogViewModel: FoodLogViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val coachViewModel: CoachViewModel = viewModel()

    val onNavigateToPaywall: () -> Unit = {
        navController.navigate(Screen.Paywall.route)
    }

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Dashboard.route
    ) {
        composable(BottomNavItem.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToAddFood = { navController.navigate(BottomNavItem.AddFood.route) }
            )
        }

        composable(BottomNavItem.FoodLog.route) {
            FoodLogScreen(
                viewModel = dashboardViewModel,
                onNavigateToAddFood = { navController.navigate(BottomNavItem.AddFood.route) }
            )
        }

        composable(BottomNavItem.AddFood.route) {
            AddFoodScreen(
                viewModel = foodLogViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(BottomNavItem.Coach.route) {
            CoachScreen(
                viewModel = coachViewModel,
                isPremium = isPremium,
                onUpgradeClick = onNavigateToPaywall
            )
        }

        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                isPremium = isPremium,
                onUpgradeClick = onNavigateToPaywall
            )
        }

        composable(Screen.Paywall.route) {
            PaywallScreen(
                onClose = { navController.popBackStack() }
            )
        }
    }
}
