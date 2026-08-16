package com.toko.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toko.pos.data.Product
import com.toko.pos.ui.components.EmptyState
import com.toko.pos.utils.formatRupiah
import com.toko.pos.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit
) {
    val vm: ProductViewModel = viewModel()
    val products by vm.products.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Produk") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddProduct,
                icon = { Icon(Icons.Default.Add, "Tambah") },
                text = { Text("Tambah") }
            )
        }
    ) { padding ->
        if (products.isEmpty()) {
            EmptyState(
                message = "Belum ada produk.\nKlik + untuk menambah.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEditProduct(product.id) }
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(product.name, style = MaterialTheme.typography.titleMedium)
                                Text("Stok: ${product.stock.toInt()} ${product.unit}", style = MaterialTheme.typography.bodySmall)
                                Text(formatRupiah(product.price), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { vm.delete(product) }) {
                                Icon(Icons.Default.Delete, "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}