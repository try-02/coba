package com.toko.pos.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import com.toko.pos.data.TransactionWithItems
import com.toko.pos.utils.formatDateTime
import com.toko.pos.utils.formatRupiah

@Composable
fun StrukDialog(transaction: TransactionWithItems, onDismiss: () -> Unit) {
    val tx = transaction.transaction
    val text = buildString {
        appendLine("TOKO ANDA")
        appendLine("Jl. Merdeka No.1")
        appendLine("Telp: 08123456789")
        appendLine("--------------------------------")
        appendLine("No: ${tx.transactionNumber}")
        appendLine(formatDateTime(tx.date))
        appendLine("Kasir: ${tx.cashierId}")
        appendLine("--------------------------------")
        transaction.items.forEach { item ->
            appendLine("${item.productName}")
            appendLine("${item.quantity.toInt()} x ${formatRupiah(item.unitPrice)} = ${formatRupiah(item.subtotal)}")
        }
        appendLine("--------------------------------")
        if (tx.discountPercent > 0) {
            appendLine("Diskon ($ {tx.discountPercent}%) : -${formatRupiah(tx.discountAmount)}")
        }
        appendLine("TOTAL: ${formatRupiah(tx.total)}")
        when (tx.paymentMethod) {
            "CASH" -> {
                appendLine("Tunai: ${formatRupiah(tx.cashReceived ?: 0)}")
                appendLine("Kembalian: ${formatRupiah(tx.change ?: 0)}")
            }
            "QRIS" -> appendLine("QRIS")
            "EWALLET" -> appendLine("E-Wallet")
            "CREDIT" -> appendLine("Hutang")
        }
        appendLine("--------------------------------")
        appendLine("Terima kasih!")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Struk") },
        text = {
            Text(text, fontFamily = FontFamily.Monospace)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        }
    )
}