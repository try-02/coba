package com.sentral.org.data

/**
 * Memaksa Room membuka koneksi SQLite sejak awal lewat satu query riil.
 * (Memanggil dao() saja hanya mengambil accessor dan TIDAK membuka koneksi.)
 */
class DatabaseWarmup(private val database: PosDatabase) {
    suspend fun warm() {
        database.profilTokoDao().get()
    }
}