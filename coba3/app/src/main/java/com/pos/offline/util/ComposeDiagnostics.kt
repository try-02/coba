package com.pos.offline.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.pos.offline.BuildConfig

/**
 * Pasang HANYA pada Composable yang dicurigai
 * sebagai hotspot recomposition.
 */
@Composable
fun TrackRecomposition(componentName: String) {
    if (!BuildConfig.DEBUG) return

    val count = remember { intArrayOf(0) }

    SideEffect {
        count[0]++

        if (count[0] > 1) {
            AppLogger.d(AppLogger.TAG_LIFECYCLE) {
                "🔄 Recompose [$componentName] ke-${count[0]}"
            }
        }
    }

    DisposableEffect(componentName) {
        AppLogger.d(AppLogger.TAG_LIFECYCLE) {
            "🟢 Enter [$componentName]"
        }

        onDispose {
            AppLogger.d(AppLogger.TAG_LIFECYCLE) {
                "🔴 Dispose [$componentName] " +
                    "(Total Recompose: ${count[0]})"
            }
        }
    }
}