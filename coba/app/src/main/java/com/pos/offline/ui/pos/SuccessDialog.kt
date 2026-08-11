package com.pos.offline.ui.pos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pos.offline.data.local.entity.PaymentMethod
import com.pos.offline.data.local.entity.TransactionEntity
import com.pos.offline.data.repository.CheckoutResult
import com.pos.offline.ui.components.discountInlineLabel
import com.pos.offline.ui.components.paymentMethodLabel
import com.pos.offline.ui.receipt.PrintUiState
import com.pos.offline.util.ReceiptPrintOutcome
import com.pos.offline.util.toRupiah
import java.io.File

@Composable
internal fun SuccessDialog(
    result: CheckoutResult,
    printUiState: PrintUiState,
    openDrawerOnPrint: Boolean,
    onToggleOpenDrawer: (Boolean) -> Unit,
    onPrint: () -> Unit,
    onExport: () -> Unit,
    onSharePdfFile: (File) -> Unit,
    onNavigateToSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    // Hijau sukses standar finansial yang tegas namun nyaman di mata
    val successColor = Color(0xFF2E7D32)

    // Memastikan jika auto-print aktif di background, dialog merespons state printing secara instan[span_22](start_span)[span_22](end_span)
    val isPrinting = printUiState is PrintUiState.Printing

    Dialog(
        onDismissRequest = { if (!isPrinting) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !isPrinting,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp), // Modern card aesthetic (24dp)[span_23](start_span)[span_23](end_span)[span_24](start_span)[span_24](end_span)
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp, // Tonal elevation soft untuk kedalaman tanpa harsh shadow[span_25](start_span)[span_25](end_span)[span_26](start_span)[span_26](end_span)
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp) // 8-Point Grid padding[span_27](start_span)[span_27](end_span)[span_28](start_span)[span_28](end_span)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Peak Moment Micro-Animation dengan Glow Effect[span_29](start_span)[span_29](end_span)[span_30](start_span)[span_30](end_span)
                AnimatedCheckmark(successColor = successColor)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Transaksi Berhasil!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Transaction Summary Details Card (Struktur Struk Digital)[span_31](start_span)[span_31](end_span)[span_32](start_span)[span_32](end_span)
                TransactionSummaryBlock(result = result)

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Drawer Toggle Tile & Printer Status Banner[span_33](start_span)[span_33](end_span)
                DrawerToggleRow(
                    checked = openDrawerOnPrint,
                    onCheckedChange = onToggleOpenDrawer,
                    enabled = !isPrinting
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrintResultBanner(
                    printUiState = printUiState,
                    onSharePdfFile = onSharePdfFile,
                    onNavigateToSettings = onNavigateToSettings
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Thumb-Zone Action Buttons (Minimum 48dp Height)[span_34](start_span)[span_34](end_span)[span_35](start_span)[span_35](end_span)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isPrinting,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp), // Standar Tap Target Android[span_36](start_span)[span_36](end_span)[span_37](start_span)[span_37](end_span)
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        Text(
                            text = "Tutup",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Tombol Cetak Utama[span_38](start_span)[span_38](end_span)
                    PrintActionButton(
                        printUiState = printUiState,
                        onClick = onPrint,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Text Button Ekspor PDF[span_39](start_span)[span_39](end_span)
                TextButton(
                    onClick = onExport,
                    enabled = !isPrinting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ekspor Struk ke PDF",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionSummaryBlock(result: CheckoutResult) {
    val txChange = result.transaction.change
    val txChangeGiven = result.transaction.changeGiven
    
    // Validasi metode pembayaran[span_40](start_span)[span_40](end_span)
    val paymentMethodRaw = result.transaction.paymentMethod
    val isQris = paymentMethodRaw == PaymentMethod.QRIS.name
    val isQrisCashOut = isQris && result.transaction.changeGivenInCash

    // Container ala Struk Digital untuk memberikan konteks visual yang jelas bagi kasir[span_41](start_span)[span_41](end_span)[span_42](start_span)[span_42](end_span)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow, // 60% Neutral Base[span_43](start_span)[span_43](end_span)[span_44](start_span)[span_44](end_span)
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp) // Internal padding 16dp (8-point grid)[span_45](start_span)[span_45](end_span)[span_46](start_span)[span_46](end_span)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge Metode Pembayaran[span_47](start_span)[span_47](end_span)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isQris) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    text = paymentMethodLabel(paymentMethodRaw).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isQris) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Teks Total Pembayaran[span_48](start_span)[span_48](end_span)
            Text(
                text = "Total Pembayaran",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // 70% opacity equivalent via M3 token[span_49](start_span)[span_49](end_span)[span_50](start_span)[span_50](end_span)
            )

            // Angka Finansial Wajib Monospace[span_51](start_span)[span_51](end_span)[span_52](start_span)[span_52](end_span)
            Text(
                text = result.transaction.total.toRupiah(),
                fontFamily = FontFamily.Monospace,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )

            // Informasi Diskon (Persen / Nominal)[span_53](start_span)[span_53](end_span)
            result.transaction.discountInlineLabel()?.let { discountLabel ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = discountLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            
            // Divider bergaya Struk Pembayaran[span_54](start_span)[span_54](end_span)
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.8f),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Logika Kembalian & Kurang Bayar[span_55](start_span)[span_55](end_span)
            when {
                txChange < 0L -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Kurang Bayar",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = kotlin.math.abs(txChange).toRupiah(),
                            fontFamily = FontFamily.Monospace, // Monospace[span_56](start_span)[span_56](end_span)[span_57](start_span)[span_57](end_span)
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
                txChange == 0L -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "Uang Pas",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                else -> {
                    Text(
                        text = "Kembalian",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = txChangeGiven.toRupiah(),
                        fontFamily = FontFamily.Monospace, // Monospace[span_58](start_span)[span_58](end_span)[span_59](start_span)[span_59](end_span)
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isQrisCashOut) MaterialTheme.colorScheme.tertiary else Color(0xFF2E7D32)
                    )

                    val tip = (txChange - txChangeGiven).coerceAtLeast(0L)
                    if (tip > 0L) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Tip/Donasi: ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = tip.toRupiah(),
                                    fontFamily = FontFamily.Monospace, // Monospace[span_60](start_span)[span_60](end_span)[span_61](start_span)[span_61](end_span)
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (isQrisCashOut) {
                        Text(
                            text = "(Tunai dari Laci)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrintActionButton(
    printUiState: PrintUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPrinting = printUiState is PrintUiState.Printing

    Button(
        onClick = onClick,
        enabled = !isPrinting,
        modifier = modifier.height(48.dp), // Minimum Tap Target 48dp (Thumb-Zone)[span_62](start_span)[span_62](end_span)[span_63](start_span)[span_63](end_span)
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary, // 10% Brand Accent[span_64](start_span)[span_64](end_span)[span_65](start_span)[span_65](end_span)
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        )
    ) {
        AnimatedContent(
            targetState = isPrinting,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "print_button_animation"
        ) { printing ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (printing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Mencetak...",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Print,
                        contentDescription = "Cetak",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Cetak",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedCheckmark(successColor: Color) {
    val scaleA = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleA.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        // Subtle Background Glow Effect (Peak Moment)[span_66](start_span)[span_66](end_span)[span_67](start_span)[span_67](end_span)
        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(scaleA.value)
                .background(
                    color = successColor.copy(alpha = 0.12f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(scaleA.value)
                .background(
                    color = successColor.copy(alpha = 0.20f),
                    shape = CircleShape
                )
        )
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = "Sukses",
            tint = successColor,
            modifier = Modifier
                .size(36.dp)
                .scale(scaleA.value)
        )
    }
}

@Composable
private fun DrawerToggleRow(
    checked: Boolean, 
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    // Diubah menjadi Card Tile agar tap target mencapai standar 48dp+[span_68](start_span)[span_68](end_span)[span_69](start_span)[span_69](end_span)[span_70](start_span)[span_70](end_span)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) }
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Buka laci otomatis saat mencetak",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PrintResultBanner(
    printUiState: PrintUiState,
    onSharePdfFile: (File) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val state = printUiState as? PrintUiState.Result ?: return
    val outcome = state.outcome
    val (message, isError) = when (outcome) {
        is ReceiptPrintOutcome.Success -> "Struk berhasil dicetak di \"${outcome.printer.label}\"." to false
        is ReceiptPrintOutcome.SuccessWithNotice -> "Dicetak di \"${outcome.printer.label}\".\n⚠ ${outcome.notice}" to false
        is ReceiptPrintOutcome.Failed -> {
            val reason = outcome.attempts.firstOrNull()?.message ?: ""
            if (reason.contains("terhubung", ignoreCase = true)) {
                "Gagal mencetak. Mohon periksa koneksi perangkat." to true
            } else {
                "Gagal mencetak.\nAlasan: $reason" to true
            }
        }
        ReceiptPrintOutcome.NoPrinterConfigured -> "Printer belum diatur." to true
        ReceiptPrintOutcome.AlreadyInProgress -> "Sedang mencetak, mohon tunggu..." to false
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp), 
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            border = if (isError) {
                androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
            } else null
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.labelMedium,
                color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }

        if (outcome is ReceiptPrintOutcome.Failed && outcome.fallbackPdf != null) {
            OutlinedButton(
                onClick = { onSharePdfFile(outcome.fallbackPdf) }, 
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bagikan PDF Cadangan")
            }
        }
        if (outcome is ReceiptPrintOutcome.NoPrinterConfigured) {
            TextButton(
                onClick = onNavigateToSettings, 
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Buka Pengaturan Printer",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// PREVIEW CONTAINER FOR STUDIO DESIGN REVIEW
// ==========================================

@Preview(name = "Success Dialog - Standard Cash", showBackground = true)
@Composable
private fun SuccessDialogCashPreview() {
    MaterialTheme {
        SuccessDialog(
            result = CheckoutResult(
                transaction = TransactionEntity(
                    id = 1,
                    total = 125000L,
                    payAmount = 150000L,
                    change = 25000L,
                    changeGiven = 20000L,
                    paymentMethod = PaymentMethod.CASH.name,
                    discountInline = "10%",
                    createdAt = System.currentTimeMillis()
                )
            ),
            printUiState = PrintUiState.Idle,
            openDrawerOnPrint = true,
            onToggleOpenDrawer = {},
            onPrint = {},
            onExport = {},
            onSharePdfFile = {},
            onNavigateToSettings = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Success Dialog - QRIS Cashout", showBackground = true)
@Composable
private fun SuccessDialogQrisPreview() {
    MaterialTheme {
        SuccessDialog(
            result = CheckoutResult(
                transaction = TransactionEntity(
                    id = 2,
                    total = 85000L,
                    payAmount = 100000L,
                    change = 15000L,
                    changeGiven = 15000L,
                    changeGivenInCash = true,
                    paymentMethod = PaymentMethod.QRIS.name,
                    createdAt = System.currentTimeMillis()
                )
            ),
            printUiState = PrintUiState.Printing,
            openDrawerOnPrint = false,
            onToggleOpenDrawer = {},
            onPrint = {},
            onExport = {},
            onSharePdfFile = {},
            onNavigateToSettings = {},
            onDismiss = {}
        )
    }
}