package com.sentral.org

import android.app.Application
import com.sentral.org.data.PosDatabase
import com.sentral.org.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PosApplication : Application() {

    // 1. Ambil instance database dari Koin
    private val database: PosDatabase by inject()

    // 2. Buat scope Coroutines khusus untuk level aplikasi (berjalan di background)
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@PosApplication)
            modules(appModule)
        }

        // 3. Lakukan pemanasan database di background thread
        applicationScope.launch {
            try {
                // Memanggil salah satu DAO akan memaksa Room membuka koneksi SQLite 
                // sejak Splash Screen, sehingga UI utama tidak akan lag (freeze)
                database.profilTokoDao() 
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
