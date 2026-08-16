package com.toko.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.toko.pos.ui.navigation.AppNavHost
import com.toko.pos.ui.theme.PosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PosTheme {
                AppNavHost()
            }
        }
    }
}