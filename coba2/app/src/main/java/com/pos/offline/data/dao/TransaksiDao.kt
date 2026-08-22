package com.pos.offline.data.dao

import androidx.room3.*
import com.pos.offline.data.entity.TransaksiEntity
import com.pos.offline.data.model.StatusTransaksi
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingSource
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter

data class TransaksiDenganDetail(
    @Embedded val transaksi: TransaksiEntity,
    @Relation(parentColumn = "id", entityColumn = "transaksi_id")
    val items: List<ItemTransaksiEntity>,
    @Relation(parentColumn = "id", entityColumn = "transaksi_id")
    val pembayaran: List<PembayaranEntity>
)

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
interface TransaksiDao {
    @Insert suspend fun insert(entity: TransaksiEntity): Long
    @Transaction
    @Query("SELECT * FROM transaksi WHERE id = :id LIMIT 1") 
    suspend fun getTransaksiUtuhById(id: Long): TransaksiDenganDetail?
    @Query("SELECT * FROM transaksi WHERE nomor_transaksi = :number LIMIT 1") suspend fun getByNumber(number: String): TransaksiEntity?
    @Query("SELECT * FROM transaksi ORDER BY dibuat_pada DESC, id DESC") 
    fun observeAllPaged(): PagingSource<Int, TransaksiEntity>
    @Query("UPDATE transaksi SET status='VOID', dibatalkan_pada=:now, alasan_pembatalan=:reason WHERE id=:id AND status='SELESAI'") suspend fun markVoid(id: Long, now: Long, reason: String): Int
    @Query("SELECT EXISTS(SELECT 1 FROM pengembalian WHERE transaksi_id=:transactionId)") suspend fun hasReturns(transactionId: Long): Boolean
}
