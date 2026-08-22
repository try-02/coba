package com.pos.offline.data.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(tableName = "produk", indices = [
    Index("sku", unique = true, name = "unik_produk_sku"),
    Index("barcode", unique = true, name = "unik_produk_barcode"),
    Index("nama", name = "indeks_produk_nama"),
    Index("kategori", name = "indeks_produk_kategori"),
])
data class ProdukEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val sku: String,
    val barcode: String?,
    val harga: Long,
    val hargaModal: Long,
    val kategori: String,
    val aktif: Boolean,
    val dibuatPada: Long,
    val diperbaruiPada: Long,
)
