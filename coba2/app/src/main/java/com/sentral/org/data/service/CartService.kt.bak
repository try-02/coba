package com.sentral.org.data.service

import com.sentral.org.data.dao.ItemKeranjangDao
import com.sentral.org.data.dao.KeranjangDao
import com.sentral.org.data.dao.ProdukDao
import com.sentral.org.data.entity.ItemKeranjangEntity
import com.sentral.org.data.model.PosDataException
import com.sentral.org.data.model.StatusKeranjang
import com.sentral.org.data.model.suspendRunCatching

class CartService(
    private val write: PosWriteService,
    private val carts: KeranjangDao,
    private val items: ItemKeranjangDao,
    private val products: ProdukDao,
) {
    suspend fun addProduct(cartId: Long, productId: Long, quantity: Long, now: Long): Result<Unit> =
        suspendRunCatching {
            require(quantity > 0)
            write.run {
                val cart = carts.getById(cartId)
                    ?: throw PosDataException.NotFound("Keranjang tidak ditemukan")
                if (cart.status != StatusKeranjang.AKTIF) {
                    throw PosDataException.InvalidState("Keranjang harus AKTIF")
                }
                val product = products.getById(productId)
                    ?: throw PosDataException.NotFound("Produk tidak ditemukan")
                if (!product.aktif) throw PosDataException.Validation("Produk tidak aktif")

                // Transaksi tulis = penulis tunggal, jadi cukup UPDATE dahulu.
                // 0 baris terdampak berarti item belum ada -> INSERT.
                val updated = items.changeQuantity(
                    cartId = cartId,
                    productId = productId,
                    delta = quantity,
                    now = now,
                )
                if (updated == 0) {
                    items.insert(
                        ItemKeranjangEntity(
                            keranjangId = cartId,
                            produkId = productId,
                            namaProduk = product.nama,
                            hargaSatuan = product.harga,
                            jumlah = quantity,
                            ditambahkanPada = now,
                            diperbaruiPada = now,
                        )
                    )
                }
            }
        }

    suspend fun hold(cartId: Long, now: Long): Result<Unit> = transition { carts.hold(cartId, now) }
    suspend fun resume(cartId: Long, now: Long): Result<Unit> = transition { carts.resume(cartId, now) }
    suspend fun cancel(cartId: Long, now: Long): Result<Unit> = transition { carts.cancel(cartId, now) }

    private suspend fun transition(operation: suspend () -> Int): Result<Unit> = suspendRunCatching {
        write.run { check(operation() == 1) { "Status keranjang sudah berubah" } }
    }
}