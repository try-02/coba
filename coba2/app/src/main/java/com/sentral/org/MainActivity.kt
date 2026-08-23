// MainActivity.kt
package com.sentral.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sentral.org.ui.MainViewModel
import com.sentral.org.ui.navigation.PosNavHost

class MainActivity : ComponentActivity() {
    
    // Inisialisasi MainViewModel yang baru saja kita buat
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Pasang splash screen terlebih dahulu
        val splashScreen = installSplashScreen()
        
        // 2. TAHAN layar splash XML sampai variabel isReady bernilai true!
        // Ini memastikan main thread tidak menggambar UI sebelum aplikasi siap.
        splashScreen.setKeepOnScreenCondition {
            !mainViewModel.isReady.value
        }

        super.onCreate(savedInstanceState)
        
        setContent {
            // PosOfflineTheme { 
                
                // Compose HANYA akan merender NavHost saat Splash Screen 
                // sudah selesai menahan layar. Ini menghilangkan beban ganda.
                PosNavHost()
                
            // }
        }
    }
}
