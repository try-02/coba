package com.toko.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toko.pos.data.Product
import com.toko.pos.utils.formatRupiah
import com.toko.pos.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(productId: Long, onBack: () -> Unit) {
    val vm: ProductViewModel = viewModel()
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("pcs") }
    var barcode by remember { mutableStateOf("") }

    LaunchedEffect(productId) {
        if (productId != 0L) {
            vm.getProductById(productId)?.let {
                name = it.name
                price = it.price.toString()
                costPrice = it.costPrice.toString()
                stock = it.stock.toString()
                unit = it.unit
                barcode = it.barcode ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == 0L) "Tambah Produk" else "Edit Produk") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Produk") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = { Text("Barcode (opsional)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it.filter(Char::isDigit) },
                label = { Text("Harga Jual (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = costPrice,
                onValueChange = { costPrice = it.filter(Char::isDigit) },
                label = { Text("Harga Modal (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Stok") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Satuan") },
                    placeholder = { Text("pcs, kg, liter") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                "Harga jual: ${formatRupiah(price.toLongOrNull() ?: 0)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    if (name.isNotBlank() && price.isNotBlank()) {
                        vm.saveProduct(
                            Product(
                                id = productId,
                                name = name.trim(),
                                price = price.toLongOrNull() ?: 0,
                                costPrice = costPrice.toLongOrNull() ?: 0,
                                stock = stock.toDoubleOrNull() ?: 0.0,
                                unit = unit.ifBlank { "pcs" },
                                barcode = barcode.takeIf { it.isNotBlank() },
                                isActive = true
                            )
                        ) { onBack() }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Simpan", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}