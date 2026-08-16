package com.pos.offline.ui.inventory

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize // Pastikan Anda meng-import ini
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.offline.data.local.entity.ProductEntity
import com.pos.offline.ui.components.ThousandsSeparatorTransformation
import com.pos.offline.ui.components.rememberBarcodeScanner
import com.pos.offline.ui.components.LocalGlobalMessage
import com.pos.offline.util.ExcelManager
import com.pos.offline.util.bouncyOverscroll
import com.pos.offline.util.formatQuantity
import com.pos.offline.util.iosGlideFlingBehavior
import com.pos.offline.util.sanitizeScannedCode
import com.pos.offline.util.toRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.pos.offline.LocalActiveFocusBounds
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.composed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val form by viewModel.form.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val pendingDelete by viewModel.pendingDelete.collectAsStateWithLifecycle()
    val scanNotFound by viewModel.scanNotFound.collectAsStateWithLifecycle()
    val deletedProductFound by viewModel.deletedProductFound.collectAsStateWithLifecycle()
    val excelState by viewModel.excelState.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val globalMessage = LocalGlobalMessage.current
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val topSalesRange by viewModel.topSalesRange.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val hasCamera =
        remember {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        }
    val launchMainScanner = rememberBarcodeScanner(onScanned = viewModel::onBarcodeScanned)
    val excelExportLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.CreateDocument(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                ),
        ) { uri -> if (uri != null) viewModel.exportToExcel(uri) }
    val excelImportLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri -> if (uri != null) viewModel.importFromExcel(uri) }

LaunchedEffect(viewModel, globalMessage) {
    viewModel.messages.collect { msg ->
        globalMessage.showMessage(msg)
    }
}

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp),
                ) {
                    // Header Title Row
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                                Icon(
                                    Icons.Rounded.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Inventaris",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        if (products.isNotEmpty()) {
                                 Text(
                                    "${products.size} produk",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                 //   modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }

                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp) // Jarak antar tombol diatur di sini
                        ) {
                            CompactInventorySearchBar(
                                query = query,
                                onQueryChange = viewModel::search,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp), // Disamakan dengan tinggi di menu POS
                            )
                            
                            // 1. Tombol Tambah Produk Pindah Kesini
                            CompactActionSurface(
                                icon = Icons.Rounded.Add,
                                desc = "Tambah Produk",
                                onClick = viewModel::startAdd
                            )
                            
                            if (hasCamera) {
                                CompactActionSurface(
                                    icon = Icons.Rounded.QrCodeScanner,
                                    desc = "Scan barcode",
                                    onClick = launchMainScanner
                                )
                            }
                            
                            ExcelIconButton(
                                icon = Icons.Rounded.FileUpload,
                                desc = "Import Excel",
                                loading = excelState.isImporting,
                                onClick = {
                                    excelImportLauncher.launch(
                                        arrayOf(
                                            "application/vnd.ms-excel",
                                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                        ),
                                    )
                                }
                            )
                            
                            ExcelIconButton(
                                icon = Icons.Rounded.FileDownload,
                                desc = "Export Excel",
                                loading = excelState.isExporting,
                                onClick = { excelExportLauncher.launch(ExcelManager.suggestedExportFileName()) }
                            )
                            
                            SortMenuButton(current = sortOption, onSelect = viewModel::setSortOption)
                            
                            if (sortOption == ProductSortOption.TERLARIS) {
                                TopSalesRangePicker(
                                    selected = topSalesRange,
                                    onSelect = viewModel::setTopSalesRange,
                                )
                            }
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets.statusBars.union(WindowInsets.navigationBars),
        ) { inner ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .consumeWindowInsets(inner)
                    .imePadding(),
            ) {
                if (products.isEmpty()) {
                    EmptyInventory(
                        hasQuery = query.isNotEmpty(),
                        isTopSalesEmpty = sortOption == ProductSortOption.TERLARIS,
                        topSalesRangeLabel = topSalesRange.label,
                    )
                } else {
                    @OptIn(ExperimentalFoundationApi::class)
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null,
                    ) {
                        LazyColumn(
                            // 1. Hapus padding samping dan jadikan jarak antar item 0.dp agar flat
                            contentPadding = PaddingValues(bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier.bouncyOverscroll(),
                            flingBehavior = iosGlideFlingBehavior(),
                        ) {
                            items(
                                items = products,
                                key = { it.id },
                                contentType = { "product" },
                            ) { product ->
                                ProductRow(
                                    product = product,
                                    onEdit = { viewModel.startEdit(product) },
                                )
                                // 2. Tambahkan garis batas tipis yang seragam dengan layar POS Kasir
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    form?.let { state ->
        ProductFormDialog(
            state = state,
            categories = categories,
            isSaving = isSaving,
            onSave = viewModel::save,
            onDismiss = viewModel::dismissForm,
            checkBarcodeConflict = viewModel::checkBarcodeConflict,
            checkSkuConflict = viewModel::checkSkuConflict,
            onDeleteRequest = { viewModel.requestDeleteFromForm(state.id) },
            onScanError = { msg -> globalMessage.showMessage(msg)},
            onReturnDamaged = { qty -> viewModel.returnDamagedItemToSupplier(state.id, qty) },
        )
    }

    pendingDelete?.let { target ->
        AlertDialog(
            onDismissRequest = viewModel::cancelDelete,
            confirmButton = {
                Button(
                    onClick = viewModel::confirmDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelDelete) { Text("Batal") }
            },
            title = { Text("Hapus Produk?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = { Text("\"${target.name}\" akan dihapus dari katalog.", style = MaterialTheme.typography.bodyMedium) },
        )
    }

    scanNotFound?.let { state ->
        AlertDialog(
            onDismissRequest = viewModel::dismissScanNotFound,
            title = { Text("Produk Tidak Ditemukan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Text("Barcode \"${state.barcode}\" belum terdaftar di katalog produk.", style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                Button(
                    onClick = viewModel::startAddFromScanned,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tambah Produk Baru", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissScanNotFound) { Text("Batal") }
            },
        )
    }

    deletedProductFound?.let { state ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDeletedProductFound,
            title = { Text("Produk Pernah Dihapus", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Barcode ini pernah digunakan oleh produk \"${state.product.name}\" yang sudah dihapus. Apakah Anda ingin memulihkannya?",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(onClick = viewModel::restoreDeletedProduct, shape = RoundedCornerShape(8.dp)) {
                    Text("Pulihkan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeletedProductFound) { Text("Batal") }
            },
        )
    }

    if (excelState.showReviewDialog) {
        ImportReviewDialog(
            reviewItems = excelState.reviewItems,
            parseErrors = excelState.parseErrors,
            isCommitting = excelState.isCommitting,
            onConfirm = viewModel::commitImport,
            onDismiss = viewModel::dismissReviewDialog,
        )
    }
}

@Composable
private fun CompactActionSurface(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = desc,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ExcelIconButton(
    icon: ImageVector,
    desc: String,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = !loading,
        shape = RoundedCornerShape(8.dp), // Radius disesuaikan untuk kotak 34.dp
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
            } else {
                Icon(
                    icon,
                    contentDescription = desc,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ImportReviewDialog(
    reviewItems: List<InventoryViewModel.ImportReviewItem>,
    parseErrors: List<String>,
    isCommitting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val newCount = reviewItems.count { it.status == InventoryViewModel.ImportStatus.NEW }
    val conflictCount = reviewItems.count { it.status == InventoryViewModel.ImportStatus.CONFLICT }
    val dupCount = reviewItems.count { it.status == InventoryViewModel.ImportStatus.DUPLICATE_IN_FILE }

    AlertDialog(
        onDismissRequest = { if (!isCommitting) onDismiss() },
        title = { Text("Tinjau Impor Produk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ImportStatBadge("Baru: $newCount", MaterialTheme.colorScheme.primary)
                    if (conflictCount > 0) ImportStatBadge("Konflik: $conflictCount", Color(0xFFF5A623))
                    if (dupCount > 0) ImportStatBadge("Dobel: $dupCount", MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Hanya produk berstatus \"Baru\" yang akan ditambahkan. Konflik & duplikat dilewati demi menjaga data lama.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                if (parseErrors.isNotEmpty()) {
                    Text(
                        "⚠ ${parseErrors.size} baris gagal dibaca:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                    )
                    parseErrors.take(10).forEach { err ->
                        Text(
                            err,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                }
                reviewItems.forEach { item ->
                    ImportReviewRow(item)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isCommitting && newCount > 0,
                shape = RoundedCornerShape(8.dp),
            ) {
                if (isCommitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Impor $newCount Produk", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isCommitting) {
                Text("Batal")
            }
        },
    )
}

@Composable
private fun ImportStatBadge(
    text: String,
    color: Color,
) {
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ImportReviewRow(item: InventoryViewModel.ImportReviewItem) {
    val (label, color) =
        when (item.status) {
            InventoryViewModel.ImportStatus.NEW -> "BARU" to MaterialTheme.colorScheme.primary
            InventoryViewModel.ImportStatus.CONFLICT -> "KONFLIK" to Color(0xFFF5A623)
            InventoryViewModel.ImportStatus.DUPLICATE_IN_FILE -> "DOBEL" to MaterialTheme.colorScheme.error
        }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                item.row.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "SKU: ${item.row.sku}${item.row.barcode?.let { " · $it" } ?: ""}",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.status == InventoryViewModel.ImportStatus.CONFLICT && item.conflictWith != null) {
                Text(
                    "Bentrok dg: ${item.conflictWith.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                )
            }
        }
        Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
            Text(
                label,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ProductRow(
    product: ProductEntity,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onEdit) 
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            // Baris Atas: Nama Produk Saja
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(4.dp))
            
            // Baris Bawah: Harga -> Laba -> Kategori
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 1. Format Harga: "Rp" kecil, Angka besar & menonjol (Sejajar dengan POS Kasir)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rp",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = product.price.toRupiah().replace("Rp", "").trim(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface, // Warna onSurface agar menonjol
                    )
                }
                
                // 2. Laba: Menggunakan icon tren naik dan angka (tanpa kurung/kata Laba)
                if (product.cost > 0) {
                    Spacer(Modifier.width(8.dp))
                    Text("·", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.width(8.dp))
                    
                    val profit = product.price - product.cost
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.TrendingUp,
                            contentDescription = "Laba",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = profit.toRupiah().replace("Rp", "").trim(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                // 3. Kategori
                if (product.category.isNotBlank()) {
                    Spacer(Modifier.width(8.dp))
                    Text("·", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.width(8.dp))
                    
                    CategoryBadge(category = product.category)
                }
            }
        }
        
        Spacer(Modifier.width(12.dp))
        
        // Kolom Kanan: Stok & Status Rusak
        Column(
            horizontalAlignment = Alignment.End, 
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StockBadge(stock = product.stock)
            if (product.damagedStock > 0.0) {
                DamagedStockBadge(stock = product.damagedStock)
            }
        }
    }
}

@Composable
private fun CategoryBadge(category: String) {
    Text(
        text = category,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
        color = MaterialTheme.colorScheme.tertiary, // Warna teks dipertahankan
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun StockBadge(stock: Double) {
    // Penyesuaian warna stok sesuai dengan POS Kasir
    val color = when {
        stock <= 0.0 -> MaterialTheme.colorScheme.error
        stock <= 5.0 -> Color(0xFFF5A623) // Kuning/Oranye untuk stok menipis
        else -> MaterialTheme.colorScheme.onSurfaceVariant // Sama seperti di POS Kasir (tanpa warna solid)
    }
    Text(
        text = if (stock <= 0.0) "Habis" else stock.formatQuantity(),
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        fontFamily = FontFamily.Monospace,
        color = color,
        fontWeight = if (stock <= 5.0) FontWeight.Bold else FontWeight.Medium,
        textAlign = TextAlign.End
    )
}

@Composable
private fun DamagedStockBadge(stock: Double) {
    Text(
        text = "${stock.formatQuantity()} Rusak",
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.error,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.End
    )
}

@Composable
private fun EmptyInventory(
    hasQuery: Boolean,
    isTopSalesEmpty: Boolean = false,
    topSalesRangeLabel: String = "",
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    when {
                        isTopSalesEmpty -> "Belum ada penjualan untuk ${topSalesRangeLabel.lowercase()}"
                        hasQuery -> "Produk tidak ditemukan"
                        else -> "Belum ada produk"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    when {
                        isTopSalesEmpty -> "Katalog produk tetap ada — coba pilih rentang waktu lain."
                        hasQuery -> "Coba gunakan kata kunci pencarian atau SKU lain."
                        else -> "Ketuk tombol + di kanan bawah untuk menambah produk pertama Anda."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CompactInventorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle =
            MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
        // 1. Tempelkan pelacak fokus
        modifier = modifier.trackFocusBounds(), 
        
        // 2. Kontrol Keyboard
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { focusManager.clearFocus() },
            onDone = { focusManager.clearFocus() }
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        // Samakan dengan POS: Tanpa background abu-abu, hanya border outline
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "Cari",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Cari nama / SKU…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
                if (query.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        onClick = {
                            onQueryChange("")
                            focusManager.clearFocus()
                        },
                        shape = CircleShape,
                        color = Color.Transparent,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Hapus Pencarian",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun SortMenuButton(
    current: ProductSortOption,
    onSelect: (ProductSortOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier.height(34.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.Sort, contentDescription = "Urutkan", modifier = Modifier.size(16.dp))
                Text(
                    current.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ProductSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (option == current) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    leadingIcon = {
                        if (option == current) {
                            Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ProductFormDialog(
    state: ProductFormState,
    categories: List<String>,
    isSaving: Boolean,
    onSave: (ProductFormState) -> Unit,
    onDismiss: () -> Unit,
    checkBarcodeConflict: suspend (String, Long) -> String?,
    checkSkuConflict: suspend (String, Long) -> String?,
    onDeleteRequest: () -> Unit,
    onScanError: (String) -> Unit,
    onReturnDamaged: (Double) -> Unit,
) {
    var name by remember(state.id) { mutableStateOf(state.name) }
    var sku by remember(state.id) { mutableStateOf(state.sku) }
    var barcode by remember(state.id) { mutableStateOf(state.barcode) }
    var category by remember(state.id) { mutableStateOf(state.category) }
    var price by remember(state.id) {
        mutableStateOf(if (state.price > 0) state.price.toString() else "")
    }
    var stock by remember(state.id) {
        mutableStateOf(if (state.isNew && state.stock == 0.0) "" else state.stock.formatQuantity())
    }
    var cost by remember(state.id) {
        mutableStateOf(if (state.cost > 0) state.cost.toString() else "")
    }
    var barcodeConflict by remember(state.id) { mutableStateOf<String?>(null) }
    var skuConflict by remember(state.id) { mutableStateOf<String?>(null) }
    var isCheckingBarcode by remember(state.id) { mutableStateOf(false) }
    var isCheckingSku by remember(state.id) { mutableStateOf(false) }
    var showReturnDamagedDialog by remember(state.id) { mutableStateOf(false) }

    val context = LocalContext.current
    val hasCamera =
        remember {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        }
    val launchScanner =
        rememberBarcodeScanner(
            onScanned = { code ->
                val sanitized = sanitizeScannedCode(code)
                if (sanitized != null) {
                    barcode = sanitized
                    sanitized
                } else {
                    onScanError("Gagal memindai kode. Coba pindai ulang.")
                    null
                }
            },
        )
    val configuration = LocalConfiguration.current
    val maxContentHeight = (configuration.screenHeightDp * 0.75f).dp
    val scrollState = rememberScrollState()

    LaunchedEffect(barcode) {
        val trimmed = barcode.trim()
        if (trimmed.isBlank()) {
            barcodeConflict = null
            isCheckingBarcode = false
            return@LaunchedEffect
        }
        isCheckingBarcode = true
        delay(300)
        barcodeConflict = checkBarcodeConflict(trimmed, state.id)
        isCheckingBarcode = false
    }

    LaunchedEffect(sku) {
        val trimmed = sku.trim()
        if (trimmed.isBlank()) {
            skuConflict = null
            isCheckingSku = false
            return@LaunchedEffect
        }
        isCheckingSku = true
        delay(300)
        skuConflict = checkSkuConflict(trimmed, state.id)
        isCheckingSku = false
    }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = {
            Text(
                if (state.isNew) "Tambah Produk" else "Edit Produk",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            val priceLong = price.toLongOrNull() ?: 0L
            val costLong = cost.toLongOrNull() ?: 0L
            Column(
                modifier =
                    Modifier
                        .heightIn(max = maxContentHeight)
                        .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Produk *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), // Tambahan Keyboard Options
                    textStyle = MaterialTheme.typography.bodyMedium,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().trackFocusBounds(), // Tambahan Tracker
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        isError = skuConflict != null,
                        supportingText =
                            if (skuConflict != null) {
                                {
                                    Text(
                                        "Dipakai oleh: $skuConflict",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }
                            } else {
                                null
                            },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).trackFocusBounds(),
                    )
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        isError = barcodeConflict != null,
                        supportingText =
                            if (barcodeConflict != null) {
                                {
                                    Text(
                                        "Dipakai oleh: $barcodeConflict",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }
                            } else {
                                null
                            },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.2f).trackFocusBounds(),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp)) {
                                if (barcode.isNotEmpty()) {
                                    IconButton(onClick = { barcode = "" }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Rounded.Close, contentDescription = "Hapus", modifier = Modifier.size(18.dp))
                                    }
                                }
                                if (hasCamera) {
                                    IconButton(onClick = launchScanner, modifier = Modifier.size(32.dp)) {
                                        Icon(
                                            Icons.Rounded.QrCodeScanner,
                                            contentDescription = "Scan",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }
                            }
                        },
                    )
                }
                
                CategoryField(
                    value = category,
                    suggestions = categories,
                    onValueChange = { category = it },
                    modifier = Modifier.trackFocusBounds() // Tempelkan Tracker
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // MoneyNumberField sekarang otomatis menggunakan tracker di dalamnya
                    MoneyNumberField(price, { price = it }, "Harga Jual", Modifier.weight(1f))
                    MoneyNumberField(cost, { cost = it }, "Modal", Modifier.weight(1f))
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // DecimalNumberField sekarang otomatis menggunakan tracker di dalamnya
                    DecimalNumberField(stock, { stock = it }, "Stok", Modifier.weight(1f))
                    
                    OutlinedTextField(
                        value = (priceLong - costLong).toRupiah(),
                        onValueChange = {},
                        readOnly = true, // Read-Only tidak perlu diberikan trackFocusBounds
                        label = { Text("Laba/Unit") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    )
                }
                if (!state.isNew && state.damagedStock > 0.0) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Stok Rusak/Garansi",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                                Text(
                                    "${state.damagedStock.formatQuantity()} item tidak layak jual",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                            Button(
                                onClick = { showReturnDamagedDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text("Retur Pabrik", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
            }
        },
        dismissButton =
            if (!state.isNew) {
                {
                    Button(
                        enabled = !isSaving,
                        onClick = onDeleteRequest,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("Hapus", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onError)
                    }
                }
            } else {
                null
            },
        confirmButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Batal") }
                Spacer(Modifier.width(4.dp))
                Button(
                    enabled =
                        !isSaving && !isCheckingBarcode && !isCheckingSku &&
                            name.isNotBlank() && barcodeConflict == null && skuConflict == null,
                    onClick = {
                        onSave(
                            ProductFormState(
                                id = state.id,
                                name = name,
                                sku = sku,
                                barcode = barcode.trim(),
                                category = category.trim(),
                                price = price.toLongOrNull() ?: 0L,
                                cost = cost.toLongOrNull() ?: 0L,
                                stock = stock.toDoubleOrNull() ?: 0.0,
                                createdAt = state.createdAt,
                            ),
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
    )

    if (showReturnDamagedDialog) {
        var returnQtyStr by remember { mutableStateOf(state.damagedStock.formatQuantity()) }
        AlertDialog(
            onDismissRequest = { showReturnDamagedDialog = false },
            title = { Text("Retur ke Pabrik/Supplier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Berapa banyak stok rusak yang ingin dikembalikan ke pabrik/dibuang?",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    DecimalNumberField(
                        value = returnQtyStr,
                        onValueChange = { returnQtyStr = it },
                        label = "Jumlah Retur",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = returnQtyStr.toDoubleOrNull() ?: 0.0
                        if (qty > 0 && qty <= state.damagedStock) {
                            onReturnDamaged(qty)
                            showReturnDamagedDialog = false
                        } else {
                            onScanError("Jumlah tidak valid. Maksimal ${state.damagedStock.formatQuantity()}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Konfirmasi Retur", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDamagedDialog = false }) { Text("Batal") }
            },
        )
    }
}

@Composable
private fun DecimalNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            val cleaned =
                buildString {
                    var dotSeen = false
                    for (c in input) {
                        when {
                            c.isDigit() -> {
                                append(c)
                            }
                            c == '.' && !dotSeen -> {
                                append(c)
                                dotSeen = true
                            }
                        }
                    }
                }.take(10)
            onValueChange(cleaned)
        },
        label = { Text(label) },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done // Aksi "Selesai" karena ini kolom terakhir
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() } // Menutup keyboard saat Selesai
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.trackFocusBounds(),
    )
}

@Composable
private fun CategoryField(
    value: String,
    suggestions: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered =
        remember(value, suggestions) {
            if (value.isBlank()) {
                suggestions
            } else {
                suggestions.filter { it.contains(value, ignoreCase = true) }
            }
        }
    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            // 1. HAPUS pemicu 'expanded' otomatis saat mengetik
            onValueChange = { onValueChange(it) }, 
            label = { Text("Kategori (opsional)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                if (suggestions.isNotEmpty()) {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Pilih kategori",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            },
            // 2. HAPUS onFocusChanged yang memicu dropdown otomatis
            modifier = modifier.fillMaxWidth() 
        )
        DropdownMenu(
            expanded = expanded && filtered.isNotEmpty(),
            onDismissRequest = { expanded = false },
        ) {
            filtered.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onValueChange(cat)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun MoneyNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() }) },
        label = { Text(label) },
        prefix = { Text("Rp ", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next // Aksi "Lanjut" (pindah ke input stok)
        ),
        visualTransformation = ThousandsSeparatorTransformation,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.trackFocusBounds(),
    )
}

@Composable
private fun TopSalesRangePicker(
    selected: TopSalesRange,
    onSelect: (TopSalesRange) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.height(34.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            TopSalesRange.entries.forEach { range ->
                TopSalesChip(
                    label = range.label,
                    selected = selected == range,
                    onClick = { onSelect(range) },
                )
            }
        }
    }
}

@Composable
private fun TopSalesChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            )
        }
    }
}

fun Modifier.trackFocusBounds(): Modifier = composed {
    val activeBounds = LocalActiveFocusBounds.current
    var isFocused by remember { mutableStateOf(false) }

    this
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
            if (!isFocused) {
                activeBounds.value = null 
            }
        }
        .onGloballyPositioned { coords ->
            if (isFocused) {
                activeBounds.value = coords.boundsInRoot() 
            }
        }
}
