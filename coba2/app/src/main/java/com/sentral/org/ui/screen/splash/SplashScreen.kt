package com.sentral.org.ui.screen.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Menjalankan proses latar belakang saat layar ini pertama kali muncul
    LaunchedEffect(key1 = Unit) {
        // Beri waktu sistem 1 detik untuk menyiapkan Database & Koin
        delay(3000) 
        onSplashFinished()
    }

    // Tampilan yang sangat ringan, tidak membebani main thread
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sentral POS Memuat...",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
