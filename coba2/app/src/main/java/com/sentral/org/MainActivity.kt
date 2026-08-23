package com.sentral.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sentral.org.ui.navigation.PosNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // PENTING: installSplashScreen() harus dipanggil SEBELUM super.onCreate()
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        setContent {
            // PosOfflineTheme { 
                PosNavHost()
            // }
        }
    }
}
