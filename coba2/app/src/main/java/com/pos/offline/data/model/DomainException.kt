package com.pos.offline.data.model

sealed class PosDataException(message: String) : IllegalStateException(message) {
    class NotFound(message: String) : PosDataException(message)
    class InvalidState(message: String) : PosDataException(message)
    class Validation(message: String) : PosDataException(message)
    class InsufficientDamagedStock(message: String) : PosDataException(message)
    class Duplicate(message: String) : PosDataException(message)
    class ProductInactive(val productId: Long, val productName: String) : PosDataException("Produk '$productName' tidak aktif. Hapus dari keranjang untuk melanjutkan.")
}
inline fun <T> suspendRunCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (c: kotlinx.coroutines.CancellationException) {
        throw c // Propagasi pembatalan coroutine
    } catch (e: Exception) {
        Result.failure(e)
    }
}