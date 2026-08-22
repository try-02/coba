package com.pos.offline.data.repository.impl

import com.pos.offline.data.dao.KasirDao
import com.pos.offline.data.repository.KasirRepository

class OfflineKasirRepository(private val dao: KasirDao) : KasirRepository {
    override fun observeAktif() = dao.observeAktif()
    override suspend fun getById(id: Long) = dao.getById(id)
}
