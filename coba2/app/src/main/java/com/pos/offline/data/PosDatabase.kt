// PosDatabase.kt
package com.pos.offline.data

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pos.offline.data.dao.*
import com.pos.offline.data.entity.*

@Database(
    entities = [
        ProdukEntity::class,
        PersediaanEntity::class,
        PergerakanPersediaanEntity::class,
        KasirEntity::class,
        ShiftEntity::class,
        PergerakanKasEntity::class,
        KeranjangEntity::class,
        ItemKeranjangEntity::class,
        TransaksiEntity::class,
        ItemTransaksiEntity::class,
        PembayaranEntity::class,
        PengembalianEntity::class,
        ItemPengembalianEntity::class,
        PrinterEntity::class,
        ProfilTokoEntity::class
    ],
    version = 1,
    exportSchema = true
)
// Tidak perlu anotasi converter sama sekali. Room 3.0 akan menangani Enum secara otomatis.
abstract class PosDatabase : RoomDatabase() {
    abstract fun produkDao(): ProdukDao
    abstract fun persediaanDao(): PersediaanDao
    abstract fun pergerakanPersediaanDao(): PergerakanPersediaanDao
    abstract fun kasirDao(): KasirDao
    abstract fun shiftDao(): ShiftDao
    abstract fun pergerakanKasDao(): PergerakanKasDao
    abstract fun keranjangDao(): KeranjangDao
    abstract fun itemKeranjangDao(): ItemKeranjangDao
    abstract fun transaksiDao(): TransaksiDao
    abstract fun itemTransaksiDao(): ItemTransaksiDao
    abstract fun pembayaranDao(): PembayaranDao
    abstract fun returDao(): ReturDao
    abstract fun printerDao(): PrinterDao
    abstract fun profilTokoDao(): ProfilTokoDao
}
