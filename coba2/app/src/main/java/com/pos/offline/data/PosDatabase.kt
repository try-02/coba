package com.pos.offline.data

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pos.offline.data.converter.DatabaseConverters
import com.pos.offline.data.entity.*

@Database(
    entities = [
        TransaksiEntity::class,
        ItemTransaksiEntity::class,
        PembayaranEntity::class,
        PengembalianEntity::class,
        ItemPengembalianEntity::class,
        PrinterEntity::class,
        ProfilTokoEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ColumnTypeConverters(DatabaseConverters::class)
abstract class PosDatabase : RoomDatabase() {
    abstract fun transaksiDao(): com.pos.offline.data.dao.TransaksiDao
    abstract fun itemTransaksiDao(): com.pos.offline.data.dao.ItemTransaksiDao
    abstract fun pembayaranDao(): com.pos.offline.data.dao.PembayaranDao
    abstract fun returDao(): com.pos.offline.data.dao.ReturDao
    abstract fun printerDao(): com.pos.offline.data.dao.PrinterDao
    abstract fun profilTokoDao(): com.pos.offline.data.dao.ProfilTokoDao
}