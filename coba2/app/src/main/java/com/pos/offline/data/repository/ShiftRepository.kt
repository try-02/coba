package com.pos.offline.data.repository

import com.pos.offline.data.entity.ShiftEntity

interface ShiftRepository {
    suspend fun getById(id: Long): ShiftEntity?
    suspend fun getOpenForKasir(kasirId: Long): ShiftEntity?
}
