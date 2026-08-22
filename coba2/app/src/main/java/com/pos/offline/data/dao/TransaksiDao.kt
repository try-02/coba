// TransaksiDao.kt
package com.pos.offline.data.dao

import androidx.room3.*
import com.pos.offline.data.entity.ItemTransaksiEntity
import com.pos.offline.data.entity.PembayaranEntity
import com.pos.offline.data.entity.TransaksiEntity
import kotlinx.coroutines.flow.Flow

data class TransaksiDenganDetail(
    @Embedded val transaksi: TransaksiEntity,
    @Relation(parentColumn = "id", entityColumn = "transaksi_id")
    val items: List<ItemTransaksiEntity>,
    @Relation(parentColumn = "id", entityColumn = "transaksi_id")
    val pembayaran: List<PembayaranEntity>
)

@Dao // Hapus @DaoReturnTypeConverters sementara
interface TransaksiDao {
    @Insert
    suspend fun insert(entity: TransaksiEntity): Long

    @Transaction
    @Query("SELECT * FROM transaksi WHERE id = :id LIMIT 1")
    suspend fun getTransaksiUtuhById(id: Long): TransaksiDenganDetail?

    @Query("SELECT * FROM transaksi WHERE nomor_transaksi = :number LIMIT 1")
    suspend fun getByNumber(number: String): TransaksiEntity?

    // KEMBALIKAN KE FLOW UNTUK TESTING
    @Query("SELECT * FROM transaksi ORDER BY dibuat_pada DESC, id DESC")
    fun observeAll(): Flow<List<TransaksiEntity>>

    @Query("UPDATE transaksi SET status = 'VOID', dibatalkan_pada = :now, alasan_pembatalan = :reason WHERE id = :id AND status = 'SELESAI'")
    suspend fun markVoid(id: Long, now: Long, reason: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM pengembalian WHERE transaksi_id = :transactionId)")
    suspend fun hasReturns(transactionId: Long): Boolean
}
