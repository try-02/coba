package com.pos.offline.ui.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.min
import com.pos.offline.data.local.entity.CashierEntity
import com.pos.offline.data.repository.ShiftSummary
import com.pos.offline.ui.components.ThousandsSeparatorTransformation
import com.pos.offline.util.toRupiah

// ==========================================
// 1. START SHIFT DIALOG (Mulai Hari Kerja)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StartShiftBottomSheet(
    cashiers: List<CashierEntity>,
    isProcessing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (cashierId: Long, startingCash: Long) -> Unit,
) {
    var selectedCashier by remember { mutableStateOf(cashiers.firstOrNull()) }
    var startingCash by remember { mutableStateOf(0L) }
    
    // Konfigurasi BottomSheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(cashiers) {
        if (selectedCashier == null || cashiers.none { it.id == selectedCashier?.id }) {
            selectedCashier = cashiers.firstOrNull()
        }
    }

    // ModalBottomSheet memindahkan aksi ke Thumb Zone (1/3 bawah layar)[span_3](start_span)[span_3](end_span)
    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        // Radius 24.dp untuk estetika modern & emotional design[span_4](start_span)[span_4](end_span)
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), 
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp), // Extra padding untuk Navigation Bar
            verticalArrangement = Arrangement.spacedBy(24.dp) // Spacing kelipatan 8[span_5](start_span)[span_5](end_span)
        ) {
            Text(
                text = "Mulai Shift Baru",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (cashiers.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Belum ada kasir terdaftar. Tambahkan kasir dulu di menu Pengaturan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Pilih Kasir",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // TextSecondary 70% opacity[span_6](start_span)[span_6](end_span)
                        )
                        CashierDropdownField(
                            cashiers = cashiers,
                            selected = selectedCashier,
                            onSelect = { selectedCashier = it }
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Modal Awal (Kas Laci)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        MoneyField(
                            label = "Modal",
                            value = startingCash,
                            onValueChange = { startingCash = it },
                            // Minimum tap target 48.dp[span_7](start_span)[span_7](end_span)
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        )
                    }
                }
            }

            // Tombol Aksi (Thumb Zone CTA)[span_8](start_span)[span_8](end_span)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Batal")
                }

                Button(
                    onClick = { selectedCashier?.let { onConfirm(it.id, startingCash) } },
                    enabled = selectedCashier != null && !isProcessing,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    // 10% Accent Color untuk CTA utama[span_9](start_span)[span_9](end_span)
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) 
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Buka Shift")
                    }
                }
            }
        }
    }
}

@Composable
internal fun EndShiftDialog(
    summary: ShiftSummary,
    isProcessing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (actualCash: Long) -> Unit,
) {
    var actualCash by remember { mutableStateOf(0L) }
    var hasBeenEdited by remember { mutableStateOf(false) }
    
    val expected = summary.expectedCashInDrawer
    val difference = actualCash - expected
    val isCleanZeroAllowed = actualCash == 0L && expected == 0L
    val hasInput = hasBeenEdited || isCleanZeroAllowed

    // --- KUNCI PERBAIKAN SIZING COLLAPSE ---
    // Mengambil ukuran layar absolut untuk menghindari perhitungan modifier Infinity pada Dialog
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    // Menggunakan 95% dari layar, tetapi dibatasi maksimal 500.dp (cocok untuk HP & Tablet)
    val dialogWidth = min(screenWidth * 0.95f, 500.dp)

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .width(dialogWidth) // Menggunakan width absolut, BUKAN fillMaxWidth
                .fillMaxHeight(0.9f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp), // Radius 24.dp untuk Peak-End Rule[span_10](start_span)[span_10](end_span)
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxSize()
            ) {
                // Header Tetap
                Text(
                    text = "Tutup Shift",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(16.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // --- BLOK 1: RINGKASAN PENJUALAN ---
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Ringkasan Penjualan",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp), 
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DetailRow("Penjualan Tunai", summary.cashRevenue.toRupiah())
                                DetailRow("Penjualan QRIS", summary.qrisRevenue.toRupiah())
                                
                                HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                                DetailRow("Total Pendapatan", summary.totalRevenue.toRupiah(), isBold = true)
                                
                                // Restorasi Data Integrity Trust[span_11](start_span)[span_11](end_span)
                                if (summary.qrisRefunds > 0L) {
                                    DetailRow(
                                        label = "Refund via QRIS",
                                        value = "- ${summary.qrisRefunds.toRupiah()}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                
                                DetailRow(
                                    label = "Laba Kotor",
                                    value = summary.grossProfit.toRupiah(),
                                    isBold = true,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                if (summary.warrantyExchangeCost > 0L) {
                                    DetailRow(
                                        label = "Biaya Klaim Garansi",
                                        value = "- ${summary.warrantyExchangeCost.toRupiah()}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // --- BLOK 2: REKONSILIASI LACI ---
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Rekonsiliasi Fisik Laci",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                DetailRow("Modal Awal (Kas)", summary.startingCash.toRupiah())
                                DetailRow("+ Penjualan Tunai Masuk", summary.cashRevenue.toRupiah())
                                if (summary.cashRefunds > 0L) {
                                    DetailRow("- Refund Tunai Keluar", "- ${summary.cashRefunds.toRupiah()}", color = MaterialTheme.colorScheme.error)
                                }
                                if (summary.qrisCashChangeOut > 0L) {
                                    DetailRow("- Kembalian Laci via QRIS", "- ${summary.qrisCashChangeOut.toRupiah()}", color = MaterialTheme.colorScheme.error)
                                }
                                HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                                DetailRow(
                                    label = "Estimasi Laci (Sistem)", 
                                    value = expected.toRupiah(), 
                                    isBold = true,
                                    valueSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    // --- BLOK 3: INPUT KASIR ---
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Hitung Uang Fisik Anda",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        MoneyField(
                            label = "Total Fisik",
                            value = actualCash,
                            onValueChange = {
                                actualCash = it
                                hasBeenEdited = true
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp) // Thumb Zone[span_18](start_span)[span_18](end_span)
                        )
                    }

                    // --- BLOK 4: EMOTIONAL DESIGN (SELISIH)[span_19](start_span)[span_19](end_span) ---
                    if (hasInput) {
                        val diffAbs = kotlin.math.abs(difference)
                        val (diffColor, diffBgColor, diffIcon, diffLabel) = when {
                            difference == 0L -> listOf(
                                Color(0xFF2E7D32), // Hijau Sukses[span_20](start_span)[span_20](end_span)
                                Color(0xFF2E7D32).copy(alpha = 0.1f),
                                Icons.Rounded.CheckCircle,
                                "Uang Pas (Sesuai Sistem)"
                            )
                            difference < 0L -> listOf(
                                MaterialTheme.colorScheme.error, // Merah Danger[span_21](start_span)[span_21](end_span)
                                MaterialTheme.colorScheme.errorContainer,
                                Icons.Rounded.Warning,
                                "Selisih Minus: -${diffAbs.toRupiah()}"
                            )
                            else -> listOf(
                                Color(0xFF2E7D32),
                                Color(0xFF2E7D32).copy(alpha = 0.1f),
                                Icons.Rounded.CheckCircle,
                                "Uang Lebih: +${diffAbs.toRupiah()}"
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = diffBgColor as Color,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = diffIcon as androidx.compose.ui.graphics.vector.ImageVector,
                                    contentDescription = null,
                                    tint = diffColor as Color,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = diffLabel.toString(),
                                    fontFamily = FontFamily.Monospace, // Monospace Finansial[span_22](start_span)[span_22](end_span)
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = diffColor
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Footer Tetap (Sticky Actions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isProcessing,
                        // Tap target minim 48.dp[span_12](start_span)[span_12](end_span)
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = { onConfirm(actualCash) },
                        enabled = hasInput && !isProcessing,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Tutup Shift")
                        }
                    }
                }
            }
        }
    }
}

// Tambahan Komponen DetailRow untuk kerapian
@Composable
private fun DetailRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace, // Monospace Finansial wajib[span_25](start_span)[span_25](end_span)
            fontSize = valueSize,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = color
        )
    }
}

// ==========================================
// 3. KOMPONEN PENDUKUNG (Thumb Zone Optimized)
// ==========================================
@Composable
internal fun MoneyField(
    label: String,
    value: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember(value) {
        mutableStateOf(if (value <= 0) "" else value.toString())
    }
    BasicTextField(
        value = text,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }
            text = digits
            onValueChange(digits.toLongOrNull() ?: 0L)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        visualTransformation = ThousandsSeparatorTransformation,
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace // Wajib Monospace[span_18](start_span)[span_18](end_span)
        ),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp), // 8-point grid[span_19](start_span)[span_19](end_span)
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rp ",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(Modifier.weight(1f).padding(start = 8.dp)) {
                    if (text.isEmpty()) {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Composable
internal fun CashierDropdownField(
    cashiers: List<CashierEntity>,
    selected: CashierEntity?,
    onSelect: (CashierEntity) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // Minimum Thumb Zone[span_20](start_span)[span_20](end_span)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selected?.name ?: "Pilih Kasir",
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            cashiers.forEach { cashier ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = cashier.name,
                            style = MaterialTheme.typography.bodyLarge
                        ) 
                    },
                    onClick = {
                        onSelect(cashier)
                        expanded = false
                    }
                )
            }
        }
    }
}
