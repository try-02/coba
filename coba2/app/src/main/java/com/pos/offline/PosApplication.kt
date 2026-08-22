// PosApplication.kt
package com.pos.offline

import android.app.Application
import com.pos.offline.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            // Aktifkan logger untuk memantau waktu injeksi di Logcat[span_14](start_span)[span_14](end_span)[span_15](start_span)[span_15](end_span)
            androidLogger(Level.DEBUG)[span_16](start_span)[span_16](end_span)
            // Suntikkan context global dengan aman[span_17](start_span)[span_17](end_span)[span_18](start_span)[span_18](end_span)
            androidContext(this@PosApplication)[span_19](start_span)[span_19](end_span)
            // Muat modul aplikasi[span_20](start_span)[span_20](end_span)
            modules(appModule)[span_21](start_span)[span_21](end_span)
        }
    }
}
