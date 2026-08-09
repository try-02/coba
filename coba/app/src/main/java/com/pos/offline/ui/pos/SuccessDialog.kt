package com.pos.offline.ui.pos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pos.offline.data.local.entity.PaymentMethod
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
    val successColor = Color(0xFF2E7D32)
    val isPrinting = printUiState is PrintUiState.Printing

    Dialog(
        onDismissRequest = { if (!isPrinting) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !isPrinting,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Checkmark Animation Header
                AnimatedCheckmark(successColor = successColor)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Transaksi Berhasil!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Details Card (Struk No, Payment Method, Amounts, Tip, Discount)
                TransactionSummaryBlock(result = result)

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Drawer Toggle Row
                DrawerToggleRow(
                    checked = openDrawerOnPrint,
                    onCheckedChange = onToggleOpenDrawer
                )

                // 4. Print Outcome Status Banner
                PrintResultBanner(
                    printUiState = printUiState,
                    onSharePdfFile = onSharePdfFile,
                    onNavigateToSettings = onNavigateToSettings
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Action Buttons (Height 48.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isPrinting,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Selesai")
                    }

                    PrintActionButton(
                        printUiState = printUiState,
                        onClick = onPrint,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = onExport,
                    enabled = !isPrinting
                ) {
                    Text("Ekspor Struk ke PDF", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun TransactionSummaryBlock(result: CheckoutResult) {
    val tx = result.transaction
    val txChange = tx.change
    val txChangeGiven = tx.changeGiven
    val isQrisCashOut = tx.paymentMethod == PaymentMethod.QRIS.name && tx.changeGivenInCash
    val tip = (txChange - txChangeGiven).coerceAtLeast(0L)
    val discountLabel = tx.discountInlineLabel()

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: No Struk & Badge Metode Bayar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No. #${tx.id}",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = paymentMethodLabel(tx.paymentMethod),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Diskon jika ada
            if (discountLabel != null) {
                Text(
                    text = discountLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Total Pembayaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = tx.total.toRupiah(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Jumlah Bayar Diterima
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bayar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = tx.paidAmount.toRupiah(),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Detail Kembalian / Tip / Kurang Bayar
            when {
                txChange < 0L -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kurang Bayar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = kotlin.math.abs(txChange).toRupiah(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                txChange == 0L -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kembali",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Rp0 (Pas)",
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isQrisCashOut) "Kembali (Tunai dari Laci)" else "Kembali",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isQrisCashOut) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isQrisCashOut) FontWeight.SemiBold else FontWeight.Normal
                        )
                        Text(
                            text = txChangeGiven.toRupiah(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isQrisCashOut) MaterialTheme.colorScheme.tertiary else Color(0xFF2E7D32)
                        )
                    }

                    if (tip > 0L) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tip",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = tip.toRupiah(),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
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
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isPrinting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text("Mencetak...")
            } else {
                Icon(
                    imageVector = Icons.Rounded.Print,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Cetak Struk")
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
        modifier = Modifier
            .size(64.dp)
            .scale(scaleA.value)
            .background(
                color = successColor.copy(alpha = 0.12f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = "Sukses",
            tint = successColor,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun DrawerToggleRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "Buka laci saat mencetak",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
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
        is ReceiptPrintOutcome.Success -> {
            "Struk terkirim ke \"${outcome.printer.label}\"." to false
        }
        is ReceiptPrintOutcome.SuccessWithNotice -> {
            "Struk terkirim ke \"${outcome.printer.label}\".\n⚠ ${outcome.notice}" to false
        }
        is ReceiptPrintOutcome.Failed -> {
            val printerCount = outcome.attempts.size
            val reason = outcome.attempts.firstOrNull()?.message ?: ""
            if (reason.contains("terhubung", ignoreCase = true)) {
                if (printerCount > 1) {
                    "Gagal mencetak ke semua printer. Mohon hubungkan ke perangkat." to true
                } else {
                    "Gagal mencetak ke printer. Mohon hubungkan ke perangkat." to true
                }
            } else {
                val title = if (printerCount > 1) "Gagal mencetak ke semua printer." else "Gagal mencetak ke printer."
                "$title\nAlasan: $reason" to true
            }
        }
        ReceiptPrintOutcome.NoPrinterConfigured -> {
            "Printer belum diatur." to true
        }
        ReceiptPrintOutcome.AlreadyInProgress -> {
            "Sedang mencetak, mohon tunggu..." to false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Start
            )
        }

        if (outcome is ReceiptPrintOutcome.Failed && outcome.fallbackPdf != null) {
            TextButton(
                onClick = { onSharePdfFile(outcome.fallbackPdf) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bagikan PDF Cadangan")
            }
        }
        if (outcome is ReceiptPrintOutcome.NoPrinterConfigured) {
            TextButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buka Pengaturan Printer")
            }
        }
    }
}