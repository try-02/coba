package com.sentral.org.domain.usecase

import com.sentral.org.data.dao.ItemKeranjangDao

class RemoveFromCartUseCase(
    private val itemsDao: ItemKeranjangDao,
) {
    suspend operator fun invoke(itemId: Long): Result<Unit> = runCatching {
        check(itemsDao.deleteById(itemId) == 1) { "Gagal menghapus item" }
    }
}
