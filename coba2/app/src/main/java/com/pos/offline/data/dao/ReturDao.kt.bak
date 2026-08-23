package com.pos.offline.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pos.offline.data.entity.ItemPengembalianEntity
import com.pos.offline.data.entity.PengembalianEntity

@Dao
interface ReturDao {

    @Insert
    suspend fun insert(entity: PengembalianEntity): Long

    @Query("""
        SELECT * FROM pengembalian
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(id: Long): PengembalianEntity?
    
    @Insert
    suspend fun insertItems(
        items: List<ItemPengembalianEntity>
    ): List<Long>
}
