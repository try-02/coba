package com.pos.offline.ui.pos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pos.offline.data.local.entity.CashierEntity
import com.pos.offline.data.repository.ShiftSummary
import com.pos.offline.ui.components.ThousandsSeparatorTransformation
import com.pos.offline.util.toRupiah

// ==========================================
// 1. START SHIFT DIALOG (Mulai Hari Kerja)
// ==========================================
@Composable
internal fun StartShiftDialog(
    cashiers: List<CashierEntity>,
    isProcessing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (cashierId: Long, startingCash: Long) -> Unit,
) {
    var selectedCashier by remember { mutableStateOf(cashiers.firstOrNull()) }
    var startingCash by remember { mutableStateOf(0L) }

    LaunchedEffect(cashiers) {
        if (selectedCashier == null || cashiers.none { it.id == selectedCashier?.id }) {
            selectedCashier = cashiers.firstOrNull()
        }
    }

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(24.dp), // Shape M3 standar dialog
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp) // 8-point grid
                ) {
                    // Header Dialog dengan Ikon Kontekstual
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.PointOfSale,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Text(
                            text = "Mulai Shift Baru",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (cashiers.isEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Belum ada kasir terdaftar. Tambahkan kasir dulu di menu Pengaturan.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Dropdown Pemilihan Kasir
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Pilih Kasir",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                CashierDropdownField(
                                    cashiers = cashiers,
                                    selected = selectedCashier,
                                    onSelect = { selectedCashier = it }
                                )
                            }

                            // Input Modal Awal Kas
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Modal Awal (Kas Laci)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                MoneyField(
                                    label = "Modal",
                                    value = startingCash,
                                    onValueChange = { startingCash = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                )
                            }
                        }
                    }

                    // Aksi Utama (Thumb Zone)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !isProcessing,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Batal",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { selectedCashier?.let { onConfirm(it.id, startingCash) } },
                            enabled = selectedCashier != null && !isProcessing,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "Buka Shift",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. END SHIFT DIALOG (Tutup Shift)
// ==========================================
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

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.92f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .fillMaxSize()
                ) {
                    // Header Dialog
                    Text(
                        text = "Tutup Shift",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(16.dp))

                    // Area Scrollable Konten Ringkasan
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // BLOK 1: RINGKASAN PENJUALAN
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Ringkasan Penjualan",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    DetailRow("Penjualan Tunai", summary.cashRevenue.toRupiah())
                                    DetailRow("Penjualan QRIS", summary.qrisRevenue.toRupiah())

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )

                                    DetailRow("Total Pendapatan", summary.totalRevenue.toRupiah(), isBold = true)

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

                        // BLOK 2: REKONSILIASI LACI
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Rekonsiliasi Fisik Laci",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    DetailRow("Modal Awal (Kas)", summary.startingCash.toRupiah())
                                    DetailRow("+ Penjualan Tunai Masuk", summary.cashRevenue.toRupiah())

                                    if (summary.cashRefunds > 0L) {
                                        DetailRow(
                                            label = "- Refund Tunai Keluar",
                                            value = "- ${summary.cashRefunds.toRupiah()}",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    if (summary.qrisCashChangeOut > 0L) {
                                        DetailRow(
                                            label = "- Kembalian Laci via QRIS",
                                            value = "- ${summary.qrisCashChangeOut.toRupiah()}",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )

                                    DetailRow(
                                        label = "Estimasi Laci (Sistem)",
                                        value = expected.toRupiah(),
                                        isBold = true,
                                        valueSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // BLOK 3: INPUT KASIR
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Hitung Uang Fisik Anda",
                                style = MaterialTheme.typography.labelLarge,
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            )
                        }

                        // BLOK 4: INDIKATOR SELISIH (Emotional Peak State)
                        if (hasInput) {
                            val diffAbs = kotlin.math.abs(difference)
                            val successGreen = Color(0xFF2E7D32)

                            val (diffColor, diffBgColor, diffIcon, diffLabel) = when {
                                difference == 0L -> listOf(
                                    successGreen,
                                    successGreen.copy(alpha = 0.12f),
                                    Icons.Rounded.CheckCircle,
                                    "Uang Pas (Sesuai Sistem)"
                                )
                                difference < 0L -> listOf(
                                    MaterialTheme.colorScheme.error,
                                    MaterialTheme.colorScheme.errorContainer,
                                    Icons.Rounded.Warning,
                                    "Selisih Minus: -${diffAbs.toRupiah()}"
                                )
                                else -> listOf(
                                    successGreen,
                                    successGreen.copy(alpha = 0.12f),
                                    Icons.Rounded.CheckCircle,
                                    "Uang Lebih: +${diffAbs.toRupiah()}"
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = diffBgColor as Color,
                                border = BorderStroke(1.dp, (diffColor as Color).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = diffIcon as ImageVector,
                                        contentDescription = null,
                                        tint = diffColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = diffLabel.toString(),
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = diffColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !isProcessing,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Batal",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { onConfirm(actualCash) },
                            enabled = hasInput && !isProcessing,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "Tutup Shift",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. KOMPONEN PENDUKUNG (Thumb Zone Optimized)
// ==========================================
@Composable
private fun DetailRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueSize: TextUnit = 14.sp,
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
            fontFamily = FontFamily.Monospace, // Monospace Finansial Wajib
            fontSize = valueSize,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = color
        )
    }
}

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
            fontFamily = FontFamily.Monospace
        ),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rp ",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
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

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // Minimum Tap Target 48dp
                .clip(RoundedCornerShape(12.dp))
                .clickable(role = Role.DropdownList) { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = selected?.name ?: "Pilih Kasir",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selected != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (cashier.id == selected?.id) FontWeight.Bold else FontWeight.Normal
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

// ==========================================
// PREVIEW COMPOSABLES FOR ANDROID STUDIO
// ==========================================
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun StartShiftDialogPreview() {
    MaterialTheme {
        StartShiftDialog(
            cashiers = listOf(
                CashierEntity(id = 1, name = "Budi Santoso"),
                CashierEntity(id = 2, name = "Siti Rahma")
            ),
            isProcessing = false,
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EndShiftDialogPreview() {
    MaterialTheme {
        EndShiftDialog(
            summary = ShiftSummary(
                startingCash = 100000L,
                cashRevenue = 450000L,
                qrisRevenue = 200000L,
                qrisRefunds = 0L,
                cashRefunds = 0L,
                qrisCashChangeOut = 0L,
                warrantyExchangeCost = 0L,
                totalCost = 350000L,
                restockedReturnsCost = 0L
            ),
            isProcessing = false,
            onDismiss = {},
            onConfirm = {}
        )
    }
}
