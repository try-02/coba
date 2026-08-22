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
        PergerakanPersediaanEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class PosDatabase : RoomDatabase() {
    abstract fun pergerakanPersediaanDao(): PergerakanPersediaanDao
}