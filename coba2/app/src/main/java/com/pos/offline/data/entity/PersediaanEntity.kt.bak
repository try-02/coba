package com.pos.offline.data.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(tableName = "persediaan", foreignKeys = [
    ForeignKey(
        entity = ProdukEntity::class,
        parentColumns = ["id"],
        childColumns = ["produk_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.NO_ACTION,
    ),
])
data class PersediaanEntity(
    @PrimaryKey @ColumnInfo(name = "produk_id") val produkId: Long,
    val jumlah: Long,
    val jumlahRusak: Long,
    val diperbaruiPada: Long,
)
