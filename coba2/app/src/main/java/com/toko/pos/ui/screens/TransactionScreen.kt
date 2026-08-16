package com.toko.pos.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toko.pos.barcode.BarcodeScannerActivity
import com.toko.pos.data.TransactionWithItems
import com.toko.pos.session.SessionManager
import com.toko.pos.ui.components.CartPanel
import com.toko.pos.ui.components.PaymentSheet
import com.toko.pos.ui.components.ProductCard
import com.toko.pos.utils.formatRupiah
import com.toko.pos.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(onBack: () -> Unit) {
    val vm: TransactionViewModel = viewModel()
    val query by vm.query.collectAsState()
    val products by vm.searchResults.collectAsState()
    val cart by vm.cart.collectAsState()
    val total by vm.totalPrice.collectAsState()

    var showPayment by rememberSaveable { mutableStateOf(false) }
    var strukTx by remember { mutableStateOf<TransactionWithItems?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val barcodeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val barcode = result.data?.getStringExtra("barcode")
            if (barcode != null) vm.updateSearch(barcode)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = {
                        barcodeLauncher.launch(Intent(context, BarcodeScannerActivity::class.java))
                    }) {
                        Icon(Icons.Default.QrCodeScanner, "Scan Barcode")
                    }
                }
            )
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                Surface(tonalElevation = 3.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Total", style = MaterialTheme.typography.bodyMedium)
                            Text(formatRupiah(total), style = MaterialTheme.typography.headlineMedium)
                        }
                        Button(
                            onClick = { showPayment = true },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("Bayar")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Row(Modifier.padding(padding).fillMaxSize()) {
            Column(Modifier.weight(1f).fillMaxHeight()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = vm::updateSearch,
                    placeholder = { Text("Cari produk / scan barcode...") },
                    leadingIcon = { Icon(Icons.Default.QrCodeScanner, null) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                if (products.isEmpty()) {
                    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Produk tidak ditemukan")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            ProductCard(product) { vm.addProduct(product) }
                        }
                    }
                }
            }
            CartPanel(
                cart = cart,
                onUpdateQty = vm::updateQuantity,
                onRemove = vm::removeProduct,
                modifier = Modifier.weight(0.8f)
            )
        }
    }

    if (showPayment) {
        PaymentSheet(
            total = total,
            onDismiss = { showPayment = false },
            onConfirm = { method, cash, discount, customerId ->
                vm.checkout(
                    paymentMethod = method,
                    cashReceived = cash,
                    discountPercent = discount,
                    customerId = customerId,
                    cashierId = SessionManager.currentCashierId ?: 1L,
                    onSuccess = { txId ->
                        showPayment = false
                        scope.launch {
                            strukTx = vm.getTransaction(txId)
                        }
                    },
                    onError = { msg ->
                        showPayment = false
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                )
            }
        )
    }

    strukTx?.let { tx ->
        StrukDialog(
            transaction = tx,
            onDismiss = { strukTx = null }
        )
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            errorMessage = null
        }
    }
}