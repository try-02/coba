package com.pos.offline.data

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pos.offline.data.converter.DatabaseConverters
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
        ProfilTokoEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ColumnTypeConverters(DatabaseConverters::class)
abstract class PosDatabase : RoomDatabase() {
    abstract fun produkDao(): com.pos.offline.data.dao.ProdukDao
    abstract fun persediaanDao(): com.pos.offline.data.dao.PersediaanDao
    abstract fun pergerakanPersediaanDao(): com.pos.offline.data.dao.PergerakanPersediaanDao
    abstract fun kasirDao(): com.pos.offline.data.dao.KasirDao
    abstract fun shiftDao(): com.pos.offline.data.dao.ShiftDao
    abstract fun pergerakanKasDao(): com.pos.offline.data.dao.PergerakanKasDao
    abstract fun keranjangDao(): com.pos.offline.data.dao.KeranjangDao
    abstract fun itemKeranjangDao(): com.pos.offline.data.dao.ItemKeranjangDao
    abstract fun transaksiDao(): com.pos.offline.data.dao.TransaksiDao
    abstract fun itemTransaksiDao(): com.pos.offline.data.dao.ItemTransaksiDao
    abstract fun pembayaranDao(): com.pos.offline.data.dao.PembayaranDao
    abstract fun returDao(): com.pos.offline.data.dao.ReturDao
    abstract fun printerDao(): com.pos.offline.data.dao.PrinterDao
    abstract fun profilTokoDao(): com.pos.offline.data.dao.ProfilTokoDao
}