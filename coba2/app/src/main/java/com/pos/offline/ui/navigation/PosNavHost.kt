// ui/navigation/PosNavHost.kt
package com.pos.offline.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.offline.ui.screen.pos.PosUtamaScreen

@Composable
fun PosNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = PosRoute.PosUtama // Sementara langsung ke POS Utama untuk testing
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<PosRoute.Splash> {
            // SplashScreen(onSplashFinished = { navController.navigate(PosRoute.LoginKasir) })
        }
        
        composable<PosRoute.LoginKasir> {
            // LoginScreen(...)
        }

        composable<PosRoute.PosUtama> {
            PosUtamaScreen(
                onNavigateToRiwayat = { 
                    navController.navigate(PosRoute.RiwayatTransaksi()) 
                },
                onNavigateToTutupShift = {
                    // Logika navigasi tutup shift
                }
            )
        }

        composable<PosRoute.RiwayatTransaksi> { backStackEntry ->
            // Contoh cara menangkap argumen (kasirId) secara type-safe dari Route
            // val route = backStackEntry.toRoute<PosRoute.RiwayatTransaksi>()
            // RiwayatScreen(kasirId = route.kasirId)
        }
    }
}
