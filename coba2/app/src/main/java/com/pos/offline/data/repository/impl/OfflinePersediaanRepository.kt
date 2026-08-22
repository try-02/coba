package com.pos.offline.data.repository.impl

import com.pos.offline.data.dao.PersediaanDao
import com.pos.offline.data.dao.PergerakanPersediaanDao
import com.pos.offline.data.repository.PersediaanRepository

class OfflinePersediaanRepository(private val stock: PersediaanDao, private val ledger: PergerakanPersediaanDao) : PersediaanRepository {
    override suspend fun getByProduct(productId: Long) = stock.getByProdukId(productId)
    override suspend fun getHistory(productId: Long) = ledger.getByProduk(productId)
}
