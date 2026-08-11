package com.pos.offline.ui.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pos.offline.data.local.entity.PrinterConnectionType
import com.pos.offline.data.local.entity.PrinterEntity

@Composable
fun PrinterPickerDialog(
    printers: List<PrinterEntity>,
    onSelect: (PrinterEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Printer Struk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Struk akan dicetak ulang ke printer yang dipilih. Jika gagal, sistem TIDAK akan mencoba printer lain secara otomatis.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(printers, key = { it.id }) { printer ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button) { onSelect(printer) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (printer.connectionType) {
                                        PrinterConnectionType.WIFI -> Icons.Rounded.Wifi
                                        PrinterConnectionType.BLUETOOTH -> Icons.Rounded.Bluetooth
                                        PrinterConnectionType.USB -> Icons.Rounded.Usb
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = printer.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = connectionSummary(printer),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace, // Monospace for technical connection summary
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.Print,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
    )
}

private fun connectionSummary(printer: PrinterEntity): String =
    when (printer.connectionType) {
        PrinterConnectionType.WIFI -> "WiFi \u2022 ${printer.wifiIpAddress}:${printer.wifiPort}"
        PrinterConnectionType.BLUETOOTH -> "Bluetooth"
        PrinterConnectionType.USB -> "USB"
    }
