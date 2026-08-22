package com.pos.offline.data.repository.impl

import com.pos.offline.data.dao.ReturDao
import com.pos.offline.data.repository.ReturRepository

class OfflineReturRepository(private val dao: ReturDao) : ReturRepository {
    override suspend fun getByTransaction(transactionId: Long) = dao.getByTransaction(transactionId)
    override suspend fun getItems(returnId: Long) = dao.getItemsByReturn(returnId)
}
