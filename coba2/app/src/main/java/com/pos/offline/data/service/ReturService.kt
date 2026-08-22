package com.pos.offline.data.service

import com.pos.offline.data.dao.*
import com.pos.offline.data.entity.*
import com.pos.offline.data.model.*

class ReturService(
    private val write: PosWriteService,
    private val transactions: TransaksiDao,
    private val transactionItems: ItemTransaksiDao,
    private val returns: ReturDao,
    private val products: ProdukDao,
    private val cashiers: KasirDao,
    private val shifts: ShiftDao,
    private val cashLedger: PergerakanKasDao,
    private val stock: PersediaanDao,
    private val inventoryLedger: PergerakanPersediaanDao,
) {
    private val inventory by lazy { InventoryMutationService(stock, inventoryLedger) }

    suspend fun process(request: ReturnRequest): Result<ReturnResult> = runCatching {
        write.run {
            if (request.lines.isEmpty()) throw PosDataException.Validation("Retur harus memiliki item")
            if (request.lines.map { it.transactionItemId }.distinct().size != request.lines.size) {
                throw PosDataException.Validation("Item transaksi duplikat dalam satu retur")
            }

            val transaction = transactions.getById(request.transactionId)
                ?: throw PosDataException.NotFound("Transaksi tidak ditemukan")
            if (transaction.status != StatusTransaksi.SELESAI) {
                throw PosDataException.InvalidState("Transaksi tidak dapat diretur")
            }

            val cashier = cashiers.getById(request.cashierId)
                ?: throw PosDataException.NotFound("Kasir tidak ditemukan")
            if (!cashier.aktif) throw PosDataException.Validation("Kasir tidak aktif")

            val shift = request.shiftId?.let {
                shifts.getById(it) ?: throw PosDataException.NotFound("Shift tidak ditemukan")
            }
            if (shift != null && (shift.status != StatusShift.TERBUKA || shift.kasirId != cashier.id)) {
                throw PosDataException.InvalidState("Shift retur tidak aktif atau bukan milik kasir")
            }

            val processed = request.lines.map { line ->
                val item = transactionItems.getById(line.transactionItemId)
                    ?: throw PosDataException.NotFound("Item transaksi tidak ditemukan")
                if (item.transaksiId != transaction.id) throw PosDataException.Validation("Item bukan milik transaksi")
                if (line.quantity <= 0) throw PosDataException.Validation("Quantity retur harus > 0")
                val returned = returns.getReturnedQuantity(item.id)
                val remaining = item.jumlah - returned
                if (line.quantity > remaining) {
                    throw PosDataException.Validation("Quantity retur melebihi sisa quantity yang dapat diretur")
                }
val netLine = item.totalBaris - item.diskonItem
// PERBAIKAN: Gunakan perhitungan proporsional yang presisi
val refund = MoneyMath.proportional(part = line.quantity, total = item.jumlah, amount = netLine)
                Triple(item, line, refund)
            }

            val totalRefund = MoneyMath.sumExact(processed.map { it.third })
            val returnId = returns.insert(
                PengembalianEntity(
                    transaksiId = transaction.id,
                    transaksiPenggantiId = request.replacementTransactionId,
                    dikembalikanPada = request.now,
                    kasirId = cashier.id,
                    shiftId = shift?.id,
                    namaKasir = cashier.nama,
                    jumlahPengembalian = totalRefund,
                    metodePengembalian = request.refundMethod,
                    catatan = request.note,
                    adalahTukarGaransi = request.warrantyExchange,
                ),
            )

            val returnItems = processed.map { (item, line, refund) ->
                ItemPengembalianEntity(
                    pengembalianId = returnId,
                    itemTransaksiId = item.id,
                    produkId = item.produkId,
                    namaProduk = item.namaProduk,
                    hargaSatuan = item.hargaSatuan,
                    jumlahDikembalikan = line.quantity,
                    jumlahRefund = refund,
                    tujuanStok = line.destination,
                )
            }
            val returnItemIds = returns.insertItems(returnItems)

            processed.forEachIndexed { index, (item, line, _) ->
                val productId = item.produkId
                    ?: throw PosDataException.Validation("Produk historical tidak tersedia untuk mutasi stok")
                when (line.destination) {
                    TujuanStokPengembalian.NORMAL -> inventory.mutateNormal(productId, line.quantity, 0, JenisPergerakanPersediaan.PENGEMBALIAN_NORMAL, returnId = returnId, returnItemId = returnItemIds[index], shiftId = shift?.id, note = "Retur transaksi ${transaction.nomorTransaksi}", now = request.now)
                    TujuanStokPengembalian.RUSAK -> inventory.mutateNormal(productId, 0, line.quantity, JenisPergerakanPersediaan.PENGEMBALIAN_RUSAK, returnId = returnId, returnItemId = returnItemIds[index], shiftId = shift?.id, note = "Retur rusak transaksi ${transaction.nomorTransaksi}", now = request.now)
                    TujuanStokPengembalian.TIDAK_DIKEMBALIKAN -> Unit
                }
            }

            if (request.refundMethod == MetodePembayaran.CASH && totalRefund > 0) {
                val sid = shift?.id ?: throw PosDataException.Validation("Refund CASH membutuhkan shift")
                cashLedger.insert(
                    PergerakanKasEntity(
                        shiftId = sid,
                        jenis = JenisPergerakanKas.RETUR,
                        jumlahDelta = -totalRefund,
                        transaksiId = transaction.id,
                        pengembalianId = returnId,
                        keterangan = "Refund retur transaksi ${transaction.nomorTransaksi}",
                        dibuatPada = request.now,
                    ),
                )
            }

            ReturnResult(returnId, totalRefund)
        }
    }
}
