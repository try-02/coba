package com.toko.pos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toko.pos.ui.models.CartItemUi
import com.toko.pos.utils.formatRupiah
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CartPanel(
    cart: ImmutableList<CartItemUi>,
    onUpdateQty: (Long, Double) -> Unit,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxHeight().padding(8.dp)) {
        Column(Modifier.fillMaxSize()) {
            Text("Keranjang", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
            HorizontalDivider()
            if (cart.isEmpty()) {
                EmptyState("Keranjang kosong")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cart, key = { it.productId }) { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(item.productName, maxLines = 1, style = MaterialTheme.typography.bodyLarge)
                                Text("${item.quantity.toInt()} x ${formatRupiah(item.price)}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onUpdateQty(item.productId, item.quantity - 1) }) {
                                Icon(Icons.Default.Remove, "Kurangi")
                            }
                            Text("${item.quantity.toInt()}", style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { onUpdateQty(item.productId, item.quantity + 1) }) {
                                Icon(Icons.Default.Add, "Tambah")
                            }
                            IconButton(onClick = { onRemove(item.productId) }) {
                                Icon(Icons.Default.Delete, "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}