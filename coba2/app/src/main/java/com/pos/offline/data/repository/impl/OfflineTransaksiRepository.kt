package com.pos.offline.data.repository.impl

import com.pos.offline.data.dao.ItemTransaksiDao
import com.pos.offline.data.dao.PembayaranDao
import com.pos.offline.data.dao.TransaksiDao
import com.pos.offline.data.entity.TransaksiEntity
import com.pos.offline.data.repository.TransaksiRepository
import kotlinx.coroutines.flow.Flow

class OfflineTransaksiRepository(private val transactions: TransaksiDao, private val items: ItemTransaksiDao, private val payments: PembayaranDao) : TransaksiRepository {
    override fun observeAll(): Flow<List<TransaksiEntity>> = transactions.observeAll()
    override suspend fun getById(id: Long) = transactions.getById(id)
    override suspend fun getItems(id: Long) = items.getByTransaction(id)
    override suspend fun getPayments(id: Long) = payments.getByTransaction(id)
}
