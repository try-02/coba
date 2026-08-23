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
    // 1. Parameter tanpa default paling atas
    onNavigateToRiwayat: () -> Unit,
    onNavigateToTutupShift: () -> Unit,
    // 2. Modifier selalu di tengah/setelah parameter wajib
    modifier: Modifier = Modifier,
    // 3. Parameter dengan default paling bawah
    viewModel: CheckoutViewModel = koinViewModel(),
) {
    val checkoutState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        // Terapkan modifier yang diterima dari luar ke root element
        modifier = modifier,
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(0.6f),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Grid Produk (Dalam Konstruksi)", modifier = Modifier.padding(16.dp))
                }
            }

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
