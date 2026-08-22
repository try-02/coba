package com.pos.offline.data.repository.impl

import com.pos.offline.data.dao.ShiftDao
import com.pos.offline.data.repository.ShiftRepository

class OfflineShiftRepository(private val dao: ShiftDao) : ShiftRepository {
    override suspend fun getById(id: Long) = dao.getById(id)
    override suspend fun getOpenForKasir(kasirId: Long) = dao.getOpenForKasir(kasirId)
}
