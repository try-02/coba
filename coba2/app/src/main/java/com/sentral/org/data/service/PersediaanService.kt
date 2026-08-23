package com.sentral.org.data.service

import com.sentral.org.data.dao.PergerakanPersediaanDao
import com.sentral.org.data.dao.PersediaanDao
import com.sentral.org.data.dao.ProdukDao
import com.sentral.org.data.entity.PergerakanPersediaanEntity
import com.sentral.org.data.entity.PersediaanEntity
import com.sentral.org.data.model.JenisPergerakanPersediaan
import com.sentral.org.data.model.PosDataException

class PersediaanService(
    private val write: PosWriteService,
    private val products: ProdukDao,
    private val stock: PersediaanDao,
    private val ledger: PergerakanPersediaanDao,
) {
    suspend fun createForProduct(productId: Long, initialQuantity: Long, initialDamaged: Long, now: Long): Result<Unit> = runCatching {
        require(initialQuantity >= 0) { "Stok awal tidak boleh negatif" }
        require(initialDamaged >= 0) { "Stok rusak awal tidak boleh negatif" }
        write.run {
            products.getById(productId) ?: throw PosDataException.NotFound("Produk tidak ditemukan")
            stock.insert(PersediaanEntity(productId, initialQuantity, initialDamaged, now))
            if (initialQuantity != 0L || initialDamaged != 0L) {
                ledger.insert(PergerakanPersediaanEntity(0, productId, JenisPergerakanPersediaan.STOK_AWAL, initialQuantity, initialDamaged, 0, initialQuantity, 0, initialDamaged, null, null, null, null, null, "Stok awal", now))
            }
        }
    }
}
