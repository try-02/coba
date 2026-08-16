package com.toko.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toko.pos.utils.formatRupiah
import com.toko.pos.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onBack: () -> Unit) {
    val vm: DashboardViewModel = viewModel()
    val today by vm.todaySummary.collectAsState()
    val yesterday by vm.yesterdaySummary.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Hari Ini", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    SummaryRow("Omzet", today?.revenue ?: 0)
                    SummaryRow("Laba Kotor", today?.profit ?: 0)
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Kemarin", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    SummaryRow("Omzet", yesterday?.revenue ?: 0)
                    SummaryRow("Laba Kotor", yesterday?.profit ?: 0)
                }
            }
            Text(
                "Laporan lengkap (harian/mingguan/bulanan) dapat dikembangkan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Long) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f))
        Text(formatRupiah(amount), style = MaterialTheme.typography.bodyLarge)
    }
}