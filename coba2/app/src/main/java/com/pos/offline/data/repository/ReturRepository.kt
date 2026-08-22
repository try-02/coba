package com.pos.offline.data.repository

import com.pos.offline.data.entity.ItemPengembalianEntity
import com.pos.offline.data.entity.PengembalianEntity

interface ReturRepository {
    suspend fun getByTransaction(transactionId: Long): List<PengembalianEntity>
    suspend fun getItems(returnId: Long): List<ItemPengembalianEntity>
}
