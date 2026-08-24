package com.sentral.org.ui.screen.pos

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentral.org.data.entity.ProdukEntity
import com.sentral.org.data.model.QUANTITY_SCALE
import com.sentral.org.data.model.StatusKeranjang
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosUtamaScreen(
    onNavigateToRiwayat: () -> Unit,
    onNavigateToTutupShift: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KasirViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var dialogBayarTerbuka by rememberSaveable { mutableStateOf(false) }
    var hasilCheckout by remember { mutableStateOf<KasirEvent.CheckoutBerhasil?>(null) }

    // Event sekali-tampil: pesan -> snackbar, checkout sukses -> dialog.
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is KasirEvent.Pesan -> snackbarHostState.showSnackbar(event.teks)
                is KasirEvent.CheckoutBerhasil -> hasilCheckout = event
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("POS Kasir Offline") },
                actions = {
                    TextButton(onClick = onNavigateToRiwayat) { Text("Riwayat") }
                    TextButton(onClick = onNavigateToTutupShift) { Text("Tutup Shift") }
                },
            )
        },
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PanelProduk(
                produk = state.produk,
                onProdukDipilih = viewModel::tambahProduk,
                modifier = Modifier.weight(0.55f),
            )
            PanelKeranjang(
                state = state,
                onKeranjangBaru = viewModel::keranjangBaru,
                onPilihKeranjang = viewModel::pilihKeranjang,
                onLanjutkan = viewModel::lanjutkanKeranjang,
                onTahan = viewModel::tahanKeranjang,
                onBatal = viewModel::batalkanKeranjang,
                onTambah = viewModel::tambahSatuan,
                onKurangi = viewModel::kurangiSatuan,
                onHapus = viewModel::hapusBaris,
                onBayarCash = { dialogBayarTerbuka = true },
                onBayarQris = viewModel::bayarQris,
                modifier = Modifier.weight(0.45f),
            )
        }
    }

    if (dialogBayarTerbuka) {
        DialogPembayaranTunai(
            total = state.subtotal,
            sedangProses = state.sedangProses,
            onKonfirmasi = { diterima ->
                dialogBayarTerbuka = false
                viewModel.bayarCash(diterima)
            },
            onTutup = { dialogBayarTerbuka = false },
        )
    }

    hasilCheckout?.let { hasil ->
        AlertDialog(
            onDismissRequest = { hasilCheckout = null },
            title = { Text("Transaksi Berhasil") },
            text = {
                Column {
                    Text(hasil.nomorTransaksi, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Kembalian: ${formatRupiah(hasil.kembalian)}", style = MaterialTheme.typography.headlineSmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { hasilCheckout = null }) { Text("OK") }
            },
        )
    }
}

// ---------- Panel kiri: grid produk ----------

@Composable
private fun PanelProduk(
    produk: List<ProdukEntity>,
    onProdukDipilih: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Text("Produk", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (produk.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Belum ada produk aktif.\nTambahkan produk & buka shift untuk mulai.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(produk, key = { it.id }) { p ->
                        ElevatedCard(onClick = { onProdukDipilih(p.id) }) {
                            Column(Modifier.padding(10.dp)) {
                                Text(
                                    p.nama,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    formatRupiah(p.harga),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Panel kanan: keranjang ----------

@Composable
private fun PanelKeranjang(
    state: KasirUiState,
    onKeranjangBaru: () -> Unit,
    onPilihKeranjang: (Long) -> Unit,
    onLanjutkan: (Long) -> Unit,
    onTahan: () -> Unit,
    onBatal: () -> Unit,
    onTambah: (Long) -> Unit,
    onKurangi: (Long) -> Unit,
    onHapus: (Long) -> Unit,
    onBayarCash: () -> Unit,
    onBayarQris: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Keranjang", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                TextButton(onClick = onKeranjangBaru) { Text("+ Baru") }
            }

            if (state.keranjangTerbuka.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    state.keranjangTerbuka.forEach { keranjang ->
                        val ditahan = keranjang.status == StatusKeranjang.DITAHAN
                        FilterChip(
                            selected = keranjang.id == state.keranjangAktifId,
                            onClick = {
                                if (ditahan) onLanjutkan(keranjang.id) else onPilihKeranjang(keranjang.id)
                            },
                            label = { Text(if (ditahan) "#${keranjang.id} ⏸" else "#${keranjang.id}") },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (state.baris.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "Keranjang kosong.\nSentuh produk untuk menambah.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    state.baris.forEach { baris ->
                        BarisItem(baris, onTambah, onKurangi, onHapus)
                    }
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Subtotal", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Text(
                    formatRupiah(state.subtotal),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onTahan,
                    enabled = state.keranjangAktifId != null && !state.sedangProses,
                    modifier = Modifier.weight(1f),
                ) { Text("Tahan") }
                OutlinedButton(
                    onClick = onBatal,
                    enabled = state.keranjangAktifId != null && !state.sedangProses,
                    modifier = Modifier.weight(1f),
                ) { Text("Batalkan") }
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onBayarCash,
                enabled = state.baris.isNotEmpty() && !state.sedangProses,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.sedangProses) "Memproses…" else "Bayar Tunai")
            }
            OutlinedButton(
                onClick = onBayarQris,
                enabled = state.baris.isNotEmpty() && !state.sedangProses,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Bayar QRIS")
            }
        }
    }
}

@Composable
private fun BarisItem(
    baris: BarisKeranjangUi,
    onTambah: (Long) -> Unit,
    onKurangi: (Long) -> Unit,
    onHapus: (Long) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(baris.nama, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${formatRupiah(baris.hargaSatuan)} × ${baris.jumlahScaled / QUANTITY_SCALE}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = { onKurangi(baris.produkId) }) {
            Icon(Icons.Default.Remove, contentDescription = "Kurangi")
        }
        Text(
            (baris.jumlahScaled / QUANTITY_SCALE).toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(28.dp),
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(onClick = { onTambah(baris.produkId) }) {
            Icon(Icons.Default.Add, contentDescription = "Tambah")
        }
        IconButton(onClick = { onHapus(baris.produkId) }) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus baris")
        }
    }
}

// ---------- Dialog pembayaran tunai ----------

@Composable
private fun DialogPembayaranTunai(
    total: Long,
    sedangProses: Boolean,
    onKonfirmasi: (Long) -> Unit,
    onTutup: () -> Unit,
) {
    var teksInput by rememberSaveable { mutableStateOf("") }
    val diterima = teksInput.filter(Char::isDigit).toLongOrNull() ?: 0L
    val selisih = diterima - total
    val cukup = total > 0 && diterima >= total

    AlertDialog(
        onDismissRequest = onTutup,
        title = { Text("Bayar Tunai") },
        text = {
            Column {
                Text("Total: ${formatRupiah(total)}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = teksInput,
                    onValueChange = { teksInput = it.filter(Char::isDigit).take(12) },
                    label = { Text("Uang diterima") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = diterima == total && total > 0, onClick = { teksInput = total.toString() }, label = { Text("Pas") })
                    FilterChip(selected = diterima == 50_000L, onClick = { teksInput = "50000" }, label = { Text("50rb") })
                    FilterChip(selected = diterima == 100_000L, onClick = { teksInput = "100000" }, label = { Text("100rb") })
                }
                Spacer(Modifier.height(8.dp))
                if (diterima > 0) {
                    Text(
                        if (cukup) "Kembalian: ${formatRupiah(selisih)}" else "Kurang ${formatRupiah(-selisih)}",
                        color = if (cukup) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = cukup && !sedangProses,
                onClick = { onKonfirmasi(diterima) },
            ) { Text(if (sedangProses) "Memproses…" else "Selesaikan") }
        },
        dismissButton = {
            TextButton(onClick = onTutup) { Text("Batal") }
        },
    )
}