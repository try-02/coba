package com.pos.offline.data.service

import com.pos.offline.data.dao.ItemKeranjangDao
import com.pos.offline.data.dao.KeranjangDao
import com.pos.offline.data.dao.ProdukDao
import com.pos.offline.data.entity.ItemKeranjangEntity
import com.pos.offline.data.model.PosDataException
import com.pos.offline.data.model.StatusKeranjang
import android.database.sqlite.SQLiteConstraintException

class CartService(
    private val write: PosWriteService,
    private val carts: KeranjangDao,
    private val items: ItemKeranjangDao,
    private val products: ProdukDao,
) {
    suspend fun addProduct(cartId: Long, productId: Long, quantity: Long, now: Long): Result<Unit> = runCatching {
        require(quantity > 0)
        write.run {
            val cart = carts.getById(cartId) ?: throw PosDataException.NotFound("Keranjang tidak ditemukan")
            if (cart.status != StatusKeranjang.AKTIF) throw PosDataException.InvalidState("Keranjang harus AKTIF")
            val product = products.getById(productId) ?: throw PosDataException.NotFound("Produk tidak ditemukan")
            if (!product.aktif) throw PosDataException.Validation("Produk tidak aktif")
// CartService.kt
// Menggunakan pola "Insert or Update" berbasis penanganan exception yang aman
val existing = items.getByProduct(cartId, productId)
if (existing == null) {
    try {
        items.insert(ItemKeranjangEntity(cartId = cartId, produkId = productId, namaProduk = product.nama, hargaSatuan = product.harga, jumlah = quantity, ditambahkanPada = now, diperbaruiPada = now))
    } catch (e: android.database.sqlite.SQLiteConstraintException) {
        // Jika tertabrak constraint unique akibat double-tap (race condition), 
        // secara elegan fallback ke operasi penambahan quantity
        check(items.changeQuantity(cartId, productId, quantity, now) == 1) { "Gagal menangani konflik keranjang" }
    }
} else {
    check(items.changeQuantity(cartId, productId, quantity, now) == 1)
}

        }
    }

    suspend fun hold(cartId: Long, now: Long): Result<Unit> = transition { carts.hold(cartId, now) }
    suspend fun resume(cartId: Long, now: Long): Result<Unit> = transition { carts.resume(cartId, now) }
    suspend fun cancel(cartId: Long, now: Long): Result<Unit> = transition { carts.cancel(cartId, now) }

    private suspend fun transition(operation: suspend () -> Int): Result<Unit> = runCatching {
        write.run { check(operation() == 1) { "Status keranjang sudah berubah" } }
    }
}
