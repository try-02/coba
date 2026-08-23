package com.pos.offline.data

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pos.offline.data.converter.DatabaseConverters
import com.pos.offline.data.dao.*
import com.pos.offline.data.entity.*

@Database(
    entities = [
        ProdukEntity::class,
        PersediaanEntity::class,
        KasirEntity::class,
        ShiftEntity::class,
        PrinterEntity::class,
        ProfilTokoEntity::class,
        TransaksiEntity::class,
        ItemTransaksiEntity::class,
        PembayaranEntity::class,
        PergerakanKasEntity::class,
        PengembalianEntity::class,
        ItemPengembalianEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@ColumnTypeConverters(DatabaseConverters::class)
abstract class PosDatabase : RoomDatabase() {

    abstract fun produkDao(): ProdukDao
    abstract fun persediaanDao(): PersediaanDao
    abstract fun kasirDao(): KasirDao
    abstract fun shiftDao(): ShiftDao
    abstract fun printerDao(): PrinterDao
    abstract fun profilTokoDao(): ProfilTokoDao
    abstract fun itemTransaksiDao(): ItemTransaksiDao
    abstract fun pembayaranDao(): PembayaranDao
}