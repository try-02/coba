package com.toko.pos

import android.app.Application
import android.util.Log
import com.toko.pos.data.AppDatabase
import com.toko.pos.data.Cashier
import com.toko.pos.data.Repository
import com.toko.pos.utils.sha256
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PosApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: Repository by lazy { Repository(database) }

    override fun onCreate() {
        super.onCreate()
        instance = this

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (database.cashierDao().getAllCashiers().firstOrNull().isEmpty()) {
                    database.cashierDao().insertCashier(
                        Cashier(
                            username = "admin",
                            passwordHash = sha256("pos_salt:admin123"),
                            name = "Administrator"
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("PosApp", "Seed data failed", e)
            }
        }
    }

    companion object {
        lateinit var instance: PosApplication
            private set
    }
}