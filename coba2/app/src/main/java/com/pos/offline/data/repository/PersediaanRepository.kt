package com.pos.offline.data.repository

import com.pos.offline.data.entity.PersediaanEntity
import com.pos.offline.data.entity.PergerakanPersediaanEntity

interface PersediaanRepository {
    suspend fun getByProduct(productId: Long): PersediaanEntity?
    suspend fun getHistory(productId: Long): List<PergerakanPersediaanEntity>
}
