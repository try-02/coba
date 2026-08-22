package com.pos.offline.data.service

import com.pos.offline.data.dao.PergerakanPersediaanDao
import com.pos.offline.data.dao.PersediaanDao
import com.pos.offline.data.entity.PergerakanPersediaanEntity
import com.pos.offline.data.model.*

class InventoryMutationService(
    private val persediaanDao: PersediaanDao,
    private val ledgerDao: PergerakanPersediaanDao,
) {
    /** Caller owns the outer write transaction. */
    suspend fun mutateNormal(
        productId: Long,
        normalDelta: Long,
        damagedDelta: Long = 0,
        type: JenisPergerakanPersediaan,
        transactionId: Long? = null,
        transactionItemId: Long? = null,
        returnId: Long? = null,
        returnItemId: Long? = null,
        shiftId: Long? = null,
        note: String = "",
        now: Long,
    ) {
        require(normalDelta != 0L || damagedDelta != 0L)
// InventoryMutationService.kt
// Lakukan Auto-Upsert: Jika baris persediaan belum ada, buat saat itu juga dengan stok 0.
// (Stok akan otomatis menjadi negatif setelah dikurangi delta penjualan)
var before = persediaanDao.getByProdukId(productId)
if (before == null) {
    persediaanDao.insert(com.pos.offline.data.entity.PersediaanEntity(
        produkId = productId, 
        jumlah = 0, 
        jumlahRusak = 0, 
        diperbaruiPada = now
    ))
    before = persediaanDao.getByProdukId(productId)!!
    
    // Opsional: Catat pergerakan sistem inisialisasi darurat di pergerakan_persediaan
    ledgerDao.insert(com.pos.offline.data.entity.PergerakanPersediaanEntity(
        produkId = productId, jenis = JenisPergerakanPersediaan.STOK_AWAL, 
        perubahanJumlah = 0, perubahanJumlahRusak = 0, saldoJumlahSebelum = 0, 
        saldoJumlahSetelah = 0, saldoRusakSebelum = 0, saldoRusakSetelah = 0, 
        transaksiId = null, itemTransaksiId = null, pengembalianId = null, 
        itemPengembalianId = null, shiftId = shiftId, 
        keterangan = "Auto-inisialisasi saat mutasi", dibuatPada = now
    ))
}
        if (normalDelta != 0L) {
            check(persediaanDao.addNormal(productId, normalDelta, now) == 1)
        }
        if (damagedDelta != 0L) {
            if (persediaanDao.addDamaged(productId, damagedDelta, now) != 1) {
                throw PosDataException.InsufficientDamagedStock("Stok rusak tidak mencukupi untuk produk $productId")
            }
        }

        val after = persediaanDao.getByProdukId(productId)
            ?: throw PosDataException.NotFound("Persediaan produk $productId tidak ditemukan setelah mutasi")

        check(after.jumlah == before.jumlah + normalDelta)
        check(after.jumlahRusak == before.jumlahRusak + damagedDelta)

        ledgerDao.insert(
            PergerakanPersediaanEntity(
                produkId = productId,
                jenis = type,
                perubahanJumlah = normalDelta,
                perubahanJumlahRusak = damagedDelta,
                saldoJumlahSebelum = before.jumlah,
                saldoJumlahSetelah = after.jumlah,
                saldoRusakSebelum = before.jumlahRusak,
                saldoRusakSetelah = after.jumlahRusak,
                transaksiId = transactionId,
                itemTransaksiId = transactionItemId,
                pengembalianId = returnId,
                itemPengembalianId = returnItemId,
                shiftId = shiftId,
                keterangan = note,
                dibuatPada = now,
            ),
        )
    }
}
