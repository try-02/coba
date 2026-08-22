package com.pos.offline.data.repository.impl

import com.pos.offline.data.dao.ProfilTokoDao
import com.pos.offline.data.repository.ProfilTokoRepository

class OfflineProfilTokoRepository(private val dao: ProfilTokoDao) : ProfilTokoRepository {
    override fun observe() = dao.observe()
    override suspend fun get() = dao.get()
    override suspend fun save(entity: com.pos.offline.data.entity.ProfilTokoEntity) = dao.save(entity)
}
