package com.toko.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toko.pos.utils.formatRupiah
import com.toko.pos.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onOpenTransaction: () -> Unit,
    onOpenProducts: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenReports: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val vm: DashboardViewModel = viewModel()
    val today by vm.todaySummary.collectAsState()
    val yesterday by vm.yesterdaySummary.collectAsState()
    val lowStock by vm.lowStockProducts.collectAsState()
    val topProducts by vm.topProducts.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Omzet Hari Ini", style = MaterialTheme.typography.titleMedium)
                    Text(formatRupiah(today?.revenue ?: 0), style = MaterialTheme.typography.headlineLarge)
                    Text("Laba: ${formatRupiah(today?.profit ?: 0)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Kemarin: ${formatRupiah(yesterday?.revenue ?: 0)}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {
                DashboardButton("Transaksi", Modifier.weight(1f), onOpenTransaction)
                Spacer(Modifier.width(8.dp))
                DashboardButton("Produk", Modifier.weight(1f), onOpenProducts)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                DashboardButton("Riwayat", Modifier.weight(1f), onOpenHistory)
                Spacer(Modifier.width(8.dp))
                DashboardButton("Laporan", Modifier.weight(1f), onOpenReports)
            }
            Spacer(Modifier.height(8.dp))
            DashboardButton("Pengaturan", Modifier.fillMaxWidth(), onOpenSettings)

            Spacer(Modifier.height(24.dp))

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Stok Kritis", style = MaterialTheme.typography.titleMedium)
                    if (lowStock.isEmpty()) {
                        Text("Semua stok aman", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        lowStock.forEach { product ->
                            Row(Modifier.padding(vertical = 4.dp)) {
                                Text(product.name, modifier = Modifier.weight(1f))
                                Text("${product.stock.toInt()} ${product.unit}")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Produk Terlaris Hari Ini", style = MaterialTheme.typography.titleMedium)
                    if (topProducts.isEmpty()) {
                        Text("Belum ada penjualan", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        topProducts.forEachIndexed { index, item ->
                            Row(Modifier.padding(vertical = 4.dp)) {
                                Text("${index + 1}. ${item.name}", modifier = Modifier.weight(1f))
                                Text("${item.totalSold.toInt()} terjual")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(80.dp)) {
        Text(text, style = MaterialTheme.typography.titleLarge)
    }
}