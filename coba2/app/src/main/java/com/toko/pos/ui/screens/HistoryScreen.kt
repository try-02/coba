package com.toko.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toko.pos.data.Transaction
import com.toko.pos.ui.components.EmptyState
import com.toko.pos.utils.formatDateTime
import com.toko.pos.utils.formatRupiah
import com.toko.pos.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val vm: HistoryViewModel = viewModel()
    val transactions by vm.transactions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        if (transactions.isEmpty()) {
            EmptyState("Belum ada transaksi", Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.id }) { tx ->
                    TransactionCard(tx)
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(tx: Transaction) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tx.transactionNumber, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.weight(1f))
                Text(formatRupiah(tx.total), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Text(formatDateTime(tx.date), style = MaterialTheme.typography.bodySmall)
            Text("Metode: ${tx.paymentMethod}", style = MaterialTheme.typography.bodySmall)
        }
    }
}