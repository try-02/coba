// MainActivity.kt
package com.pos.offline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pos.offline.ui.navigation.PosNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Bungkus dengan theme agar warna Material 3 bekerja
            // PosOfflineTheme { 
                PosNavHost()
            // }
        }
    }
}
