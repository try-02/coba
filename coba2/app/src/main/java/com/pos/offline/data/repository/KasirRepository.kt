package com.pos.offline.data.repository

import com.pos.offline.data.entity.KasirEntity
import kotlinx.coroutines.flow.Flow

interface KasirRepository {
    fun observeAktif(): Flow<List<KasirEntity>>
    suspend fun getById(id: Long): KasirEntity?
}
