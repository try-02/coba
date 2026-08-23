package com.sentral.org.domain.usecase

import com.sentral.org.data.dao.ItemKeranjangDao

class UpdateCartQuantityUseCase(
    private val itemsDao: ItemKeranjangDao,
) {
    /**
     * @param delta quantity delta in scaled units (1000 = 1 unit)
     */
    suspend operator fun invoke(cartId: Long, productId: Long, delta: Long, now: Long): Result<Unit> = runCatching {
        val affected = itemsDao.changeQuantity(cartId, productId, delta, now)
        if (affected == 0) {
            // quantity would go ≤ 0 or item not found → treat as removal
            throw IllegalStateException("Item sudah tidak tersedia atau kuantitas tidak valid")
        }
    }
}
