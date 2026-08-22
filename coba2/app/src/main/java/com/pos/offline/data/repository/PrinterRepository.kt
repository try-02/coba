package com.pos.offline.data.repository

import com.pos.offline.data.entity.PrinterEntity
import kotlinx.coroutines.flow.Flow

interface PrinterRepository {
    fun observeAll(): Flow<List<PrinterEntity>>
    suspend fun getDefault(): PrinterEntity?
}
