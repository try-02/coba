package com.toko.pos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.toko.pos.ui.screens.*

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(onLoginSuccess = {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }) }
        composable("home") { DashboardScreen(
            onOpenTransaction = { navController.navigate("transaction") },
            onOpenProducts = { navController.navigate("products") },
            onOpenHistory = { navController.navigate("history") },
            onOpenReports = { navController.navigate("reports") },
            onOpenSettings = { navController.navigate("settings") }
        ) }
        composable("transaction") { TransactionScreen(onBack = { navController.popBackStack() }) }
        composable("products") { ProductListScreen(
            onBack = { navController.popBackStack() },
            onAddProduct = { navController.navigate("products/0") },
            onEditProduct = { id -> navController.navigate("products/$id") }
        ) }
        composable(
            route = "products/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductEditScreen(productId = productId, onBack = { navController.popBackStack() })
        }
        composable("history") { HistoryScreen(onBack = { navController.popBackStack() }) }
        composable("reports") { ReportScreen(onBack = { navController.popBackStack() }) }
        composable("settings") { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}