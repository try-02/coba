package com.toko.pos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toko.pos.utils.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSheet(
    total: Long,
    onDismiss: () -> Unit,
    onConfirm: (method: String, cashReceived: Long?, discountPercent: Double, customerId: Long?) -> Unit
) {
    var selectedMethod by rememberSaveable { mutableStateOf("CASH") }
    var cash by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var selectedCustomerName by remember { mutableStateOf("Pilih Pelanggan (opsional)") }

    val discountPercent = discount.toDoubleOrNull() ?: 0.0
    val discountAmount = (total * discountPercent / 100).toLong()
    val finalTotal = total - discountAmount

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Pembayaran", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(formatRupiah(finalTotal), style = MaterialTheme.typography.displayMedium)

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = discount,
                onValueChange = { discount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Diskon (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selectedMethod == "CASH", onClick = { selectedMethod = "CASH" }, label = { Text("Tunai") })
                FilterChip(selected = selectedMethod == "QRIS", onClick = { selectedMethod = "QRIS" }, label = { Text("QRIS") })
                FilterChip(selected = selectedMethod == "EWALLET", onClick = { selectedMethod = "EWALLET" }, label = { Text("E-Wallet") })
                FilterChip(selected = selectedMethod == "CREDIT", onClick = { selectedMethod = "CREDIT" }, label = { Text("Hutang") })
            }

            if (selectedMethod == "CASH") {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = cash,
                    onValueChange = { cash = it.filter(Char::isDigit) },
                    label = { Text("Uang diterima") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                val received = cash.toLongOrNull() ?: 0
                if (received > 0) {
                    Text(
                        if (received >= finalTotal) "Kembalian: ${formatRupiah(received - finalTotal)}"
                        else "Uang kurang",
                        color = if (received >= finalTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            if (selectedMethod == "CREDIT") {
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { /* buka dialog customer */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedCustomerName)
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    onConfirm(selectedMethod, cash.toLongOrNull(), discountPercent, null)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = finalTotal > 0 && (selectedMethod != "CASH" || (cash.toLongOrNull() ?: 0) >= finalTotal)
            ) {
                Text("Bayar ${formatRupiah(finalTotal)}")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}