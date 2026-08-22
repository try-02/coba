package com.pos.offline.data.repository

import com.pos.offline.data.entity.ItemTransaksiEntity
import com.pos.offline.data.entity.PembayaranEntity
import com.pos.offline.data.entity.TransaksiEntity
import kotlinx.coroutines.flow.Flow

interface TransaksiRepository {
    fun observeAll(): Flow<List<TransaksiEntity>>
    suspend fun getById(id: Long): TransaksiEntity?
    suspend fun getItems(id: Long): List<ItemTransaksiEntity>
    suspend fun getPayments(id: Long): List<PembayaranEntity>
}
