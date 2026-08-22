package com.pos.offline.data

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pos.offline.data.converter.DatabaseConverters
import com.pos.offline.data.dao.ItemTransaksiDao
import com.pos.offline.data.dao.KasirDao
import com.pos.offline.data.dao.PembayaranDao
import com.pos.offline.data.dao.ProdukDao
import com.pos.offline.data.dao.ReturDao
import com.pos.offline.data.dao.ShiftDao
import com.pos.offline.data.dao.TransaksiDao
import com.pos.offline.data.entity.*

@Database(
    entities = [
        ProdukEntity::class,
        KasirEntity::class,
        ShiftEntity::class,
        TransaksiEntity::class,
        ItemTransaksiEntity::class,
        PembayaranEntity::class,
        PengembalianEntity::class,
        ItemPengembalianEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@ColumnTypeConverters(DatabaseConverters::class)
abstract class PosDatabase : RoomDatabase() {

    abstract fun produkDao(): ProdukDao
    abstract fun kasirDao(): KasirDao
    abstract fun shiftDao(): ShiftDao
    abstract fun transaksiDao(): TransaksiDao
    abstract fun itemTransaksiDao(): ItemTransaksiDao
    abstract fun pembayaranDao(): PembayaranDao
    abstract fun returDao(): ReturDao
}