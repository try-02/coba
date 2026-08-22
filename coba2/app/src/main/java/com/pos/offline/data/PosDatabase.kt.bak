package com.pos.offline.data

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pos.offline.data.dao.ItemKeranjangDao
import com.pos.offline.data.dao.ItemTransaksiDao
import com.pos.offline.data.dao.KasirDao
import com.pos.offline.data.dao.KeranjangDao
import com.pos.offline.data.dao.PembayaranDao
import com.pos.offline.data.dao.PergerakanKasDao
import com.pos.offline.data.dao.PergerakanPersediaanDao
import com.pos.offline.data.dao.PersediaanDao
import com.pos.offline.data.dao.PrinterDao
import com.pos.offline.data.dao.ProdukDao
import com.pos.offline.data.dao.ProfilTokoDao
import com.pos.offline.data.dao.ReturDao
import com.pos.offline.data.dao.ShiftDao
import com.pos.offline.data.dao.TransaksiDao
import com.pos.offline.data.entity.*

@Database(
    entities = [
        ProdukEntity::class,
        PersediaanEntity::class,
        KasirEntity::class,
        ShiftEntity::class,
        KeranjangEntity::class,
        PrinterEntity::class,
        ProfilTokoEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class PosDatabase : RoomDatabase() {

    abstract fun produkDao(): ProdukDao
    abstract fun persediaanDao(): PersediaanDao
    abstract fun kasirDao(): KasirDao
    abstract fun shiftDao(): ShiftDao
    abstract fun keranjangDao(): KeranjangDao
    abstract fun printerDao(): PrinterDao
    abstract fun profilTokoDao(): ProfilTokoDao
}