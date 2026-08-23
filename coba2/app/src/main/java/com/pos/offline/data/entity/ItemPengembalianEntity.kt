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
    val tujuanStok: TujuanStokPengembalian
)