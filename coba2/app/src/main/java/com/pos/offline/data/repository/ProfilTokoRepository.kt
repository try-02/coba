package com.pos.offline.data.repository

import com.pos.offline.data.entity.ProfilTokoEntity
import kotlinx.coroutines.flow.Flow

interface ProfilTokoRepository {
    fun observe(): Flow<ProfilTokoEntity?>
    suspend fun get(): ProfilTokoEntity?
    suspend fun save(entity: ProfilTokoEntity)
}
