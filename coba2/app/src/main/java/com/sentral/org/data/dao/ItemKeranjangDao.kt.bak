package com.sentral.org.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.sentral.org.data.entity.ItemKeranjangEntity

@Dao
interface ItemKeranjangDao {
    @Insert
    suspend fun insert(entity: ItemKeranjangEntity): Long
    @Query("""
        SELECT * FROM item_keranjang
        WHERE keranjang_id = :cartId ORDER BY id
    """)
    suspend fun getByCart(cartId: Long): List<ItemKeranjangEntity>
    @Query("""
        SELECT * FROM item_keranjang
        WHERE keranjang_id = :cartId AND
        produk_id = :productId LIMIT 1
    """)
    suspend fun getByProduct(cartId: Long, productId: Long): ItemKeranjangEntity?
    @Query("""
        UPDATE item_keranjang SET jumlah = jumlah + :delta, diperbarui_pada = :now
        WHERE keranjang_id = :cartId AND
        produk_id = :productId AND jumlah + :delta > 0
    """)
    suspend fun changeQuantity(cartId: Long, productId: Long, delta: Long, now: Long): Int
    @Query("""
        DELETE FROM item_keranjang
        WHERE id = :id
    """)
    suspend fun deleteById(id: Long): Int
    @Query("""
        DELETE FROM item_keranjang
        WHERE keranjang_id = :cartId
    """)
    suspend fun deleteByCart(cartId: Long): Int
}
