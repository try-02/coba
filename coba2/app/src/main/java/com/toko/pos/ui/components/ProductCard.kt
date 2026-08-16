package com.toko.pos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.toko.pos.data.Product
import com.toko.pos.utils.formatRupiah

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.barcode ?: "No barcode", style = MaterialTheme.typography.bodySmall)
                Text(formatRupiah(product.price), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
            val isLowStock = product.stock <= product.minStock
            Text(
                "Stok: ${product.stock.toInt()} ${product.unit}",
                style = MaterialTheme.typography.labelMedium,
                color = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}