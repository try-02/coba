// ui/screen/pos/PosUtamaScreen.kt
package com.sentral.org.ui.screen.pos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentral.org.data.service.CheckoutViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosUtamaScreen(
    // 1. Injeksi ViewModel otomatis dari Koin[span_1](start_span)[span_1](end_span)
    viewModel: CheckoutViewModel = koinViewModel(),
    // 2. STATE HOISTING: Jangan pernah passing NavController ke dalam screen! 
    // Gunakan fungsi lambda untuk navigasi agar screen tetap murni dan bisa di-test.
    onNavigateToRiwayat: () -> Unit,
    onNavigateToTutupShift: () -> Unit
) {
    // 3. Gunakan collectAsStateWithLifecycle untuk mencegah memory leak saat UI masuk background
    val checkoutState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("POS Kasir Offline") },
                actions = {
                    TextButton(onClick = onNavigateToRiwayat) { Text("Riwayat") }
                    TextButton(onClick = onNavigateToTutupShift) { Text("Tutup Shift") }
                }
            )
        }
    ) { paddingValues ->
        // Layout Split: 60% Produk, 40% Keranjang (Ideal untuk Tablet/POS)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Panel Kiri: Grid Produk
            Surface(
                modifier = Modifier.weight(0.6f),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Grid Produk (Dalam Konstruksi)", modifier = Modifier.padding(16.dp))
                }
            }

            // Panel Kanan: Keranjang & Pembayaran
            Surface(
                modifier = Modifier.weight(0.4f),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Keranjang Checkout (Dalam Konstruksi)", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
