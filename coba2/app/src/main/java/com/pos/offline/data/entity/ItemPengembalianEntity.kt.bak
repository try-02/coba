package com.pos.offline.data.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.pos.offline.data.model.TujuanStokPengembalian

@Entity(tableName = "item_pengembalian")
data class ItemPengembalianEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "tujuan_stok")
    val tujuanStok: TujuanStokPengembalian,

    @ColumnInfo(name = "produk_id")
    val produkId: Long?,

    @ColumnInfo(name = "item_transaksi_id")
    val itemTransaksiId: Long,

    @ColumnInfo(name = "pengembalian_id")
    val pengembalianId: Long
)