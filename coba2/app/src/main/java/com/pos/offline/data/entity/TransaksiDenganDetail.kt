package com.pos.offline.data.entity

import androidx.room3.Embedded
import androidx.room3.Relation

data class TransaksiDenganDetail(
    @Embedded val transaksi: TransaksiEntity,
    @Relation(parentColumn = "id", entityColumn = "transaksi_id")
    val items: List<ItemTransaksiEntity>,
    @Relation(parentColumn = "id", entityColumn = "transaksi_id")
    val pembayaran: List<PembayaranEntity>
)