package com.pos.offline.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pos.offline.data.local.entity.PaperWidth
import com.pos.offline.data.local.entity.PrinterConnectionType
import com.pos.offline.data.local.entity.PrinterEntity
import com.pos.offline.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterManagementDialog(
    uiState: PrinterUiState,
    printers: List<PrinterEntity>,
    onDismiss: () -> Unit,
    onOpenAdd: () -> Unit,
    onOpenEdit: (PrinterEntity) -> Unit,
    onCloseForm: () -> Unit,
    onSaveForm: () -> Unit,
    onUpdateFormLabel: (String) -> Unit,
    onUpdateFormConnectionType: (PrinterConnectionType) -> Unit,
    onUpdateFormPaperWidth: (PaperWidth) -> Unit,
    onUpdateFormCharPerLine: (String) -> Unit,
    onUpdateFormWifiIp: (String) -> Unit,
    onUpdateFormWifiPort: (String) -> Unit,
    onUpdateFormSupportsStatusQuery: (Boolean) -> Unit,
    onRequestDelete: (Long) -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onSetDefault: (PrinterEntity) -> Unit,
    onMovePriorityUp: (PrinterEntity) -> Unit,
    onMovePriorityDown: (PrinterEntity) -> Unit,
    onTestPrint: (PrinterEntity) -> Unit,
    modifier: Modifier = Modifier,
    onOpenBluetoothPicker: () -> Unit = {},
    onOpenUsbPicker: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "Kelola Printer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tutup")
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (printers.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Text(
                                "Belum ada printer ditambahkan. Tambahkan printer WiFi/LAN, Bluetooth, atau USB.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        printers.forEachIndexed { index, printer ->
                            val isTesting = uiState.testingPrinterIds.contains(printer.id) || uiState.isReordering
                            PrinterRow(
                                printer = printer,
                                isFirst = index == 0,
                                isLast = index == printers.lastIndex,
                                isTesting = isTesting,
                                onEdit = { onOpenEdit(printer) },
                                onDelete = { onRequestDelete(printer.id) },
                                onSetDefault = { onSetDefault(printer) },
                                onMoveUp = { onMovePriorityUp(printer) },
                                onMoveDown = { onMovePriorityDown(printer) },
                                onTestPrint = { onTestPrint(printer) },
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onOpenAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Tambah Printer", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    if (uiState.showFormDialog) {
        PrinterFormDialog(
            form = uiState.formState,
            isSaving = uiState.isSaving,
            onDismiss = onCloseForm,
            onSave = onSaveForm,
            onUpdateLabel = onUpdateFormLabel,
            onUpdateConnectionType = onUpdateFormConnectionType,
            onUpdatePaperWidth = onUpdateFormPaperWidth,
            onUpdateCharPerLine = onUpdateFormCharPerLine,
            onUpdateWifiIp = onUpdateFormWifiIp,
            onUpdateWifiPort = onUpdateFormWifiPort,
            onUpdateSupportsStatusQuery = onUpdateFormSupportsStatusQuery,
            onOpenBluetoothPicker = onOpenBluetoothPicker,
            onOpenUsbPicker = onOpenUsbPicker,
        )
    }

    if (uiState.pendingDeleteId != null) {
        val target = printers.find { it.id == uiState.pendingDeleteId }
        DeletePrinterConfirmDialog(
            printerLabel = target?.label.orEmpty(),
            onDismiss = onCancelDelete,
            onConfirm = onConfirmDelete,
        )
    }
}

@Composable
private fun PrinterRow(
    printer: PrinterEntity,
    isFirst: Boolean,
    isLast: Boolean,
    isTesting: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onTestPrint: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        printer.connectionType.icon(),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(printer.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    if (printer.isDefault) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                        ) {
                            Text(
                                "UTAMA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Row {
                    IconButton(onClick = onMoveUp, enabled = !isFirst && !isTesting, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Rounded.ArrowUpward, contentDescription = "Naikkan prioritas", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onMoveDown, enabled = !isLast && !isTesting, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Rounded.ArrowDownward, contentDescription = "Turunkan prioritas", modifier = Modifier.size(18.dp))
                    }
                }
            }
            Text(
                text = printerSubtitle(printer),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!printer.isDefault) {
                    TextButton(onClick = onSetDefault, enabled = !isTesting) {
                        Text("Jadikan Utama", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onTestPrint, enabled = !isTesting, modifier = Modifier.size(36.dp)) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            Icons.Rounded.Print,
                            contentDescription = "Test Print",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                IconButton(onClick = onEdit, enabled = !isTesting, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, enabled = !isTesting, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Hapus",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrinterFormDialog(
    form: PrinterFormState,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onUpdateLabel: (String) -> Unit,
    onUpdateConnectionType: (PrinterConnectionType) -> Unit,
    onUpdatePaperWidth: (PaperWidth) -> Unit,
    onUpdateCharPerLine: (String) -> Unit,
    onUpdateWifiIp: (String) -> Unit,
    onUpdateWifiPort: (String) -> Unit,
    onUpdateSupportsStatusQuery: (Boolean) -> Unit,
    onOpenBluetoothPicker: () -> Unit,
    onOpenUsbPicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEdit = form.id != null

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        modifier = modifier,
        title = {
            Text(if (isEdit) "Edit Printer" else "Tambah Printer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = form.label,
                    onValueChange = onUpdateLabel,
                    label = { Text("Nama Printer") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Jenis Koneksi", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = form.connectionType == PrinterConnectionType.WIFI,
                        onClick = { onUpdateConnectionType(PrinterConnectionType.WIFI) },
                        label = { Text("WiFi/LAN") },
                    )
                    FilterChip(
                        selected = form.connectionType == PrinterConnectionType.BLUETOOTH,
                        onClick = { onUpdateConnectionType(PrinterConnectionType.BLUETOOTH) },
                        label = { Text("Bluetooth") },
                    )
                    FilterChip(
                        selected = form.connectionType == PrinterConnectionType.USB,
                        onClick = { onUpdateConnectionType(PrinterConnectionType.USB) },
                        label = { Text("USB") },
                    )
                }

                if (form.connectionType == PrinterConnectionType.WIFI) {
                    OutlinedTextField(
                        value = form.wifiIpAddress,
                        onValueChange = onUpdateWifiIp,
                        label = { Text("Alamat IP Printer") },
                        placeholder = { Text("mis. 192.168.1.50") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = form.wifiPort,
                        onValueChange = onUpdateWifiPort,
                        label = { Text("Port") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (form.connectionType == PrinterConnectionType.BLUETOOTH) {
                    OutlinedButton(
                        onClick = onOpenBluetoothPicker,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Bluetooth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (form.bluetoothMacAddress.isBlank()) {
                                "Pilih Perangkat Bluetooth"
                            } else {
                                "Ganti (${form.bluetoothMacAddress})"
                            },
                            fontFamily = if (form.bluetoothMacAddress.isNotBlank()) FontFamily.Monospace else FontFamily.Default
                        )
                    }
                }

                if (form.connectionType == PrinterConnectionType.USB) {
                    OutlinedButton(
                        onClick = onOpenUsbPicker,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Usb, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        val vid = form.usbVendorId
                        val pid = form.usbProductId
                        Text(
                            if (vid == null || pid == null) {
                                "Pilih Perangkat USB"
                            } else {
                                "Ganti (${String.format("%04x:%04x", vid, pid)})"
                            },
                            fontFamily = if (vid != null) FontFamily.Monospace else FontFamily.Default
                        )
                    }
                }

                Text("Lebar Kertas", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = form.paperWidth == PaperWidth.MM_58,
                        onClick = { onUpdatePaperWidth(PaperWidth.MM_58) },
                        label = { Text("58mm") },
                    )
                    FilterChip(
                        selected = form.paperWidth == PaperWidth.MM_80,
                        onClick = { onUpdatePaperWidth(PaperWidth.MM_80) },
                        label = { Text("80mm") },
                    )
                }

                OutlinedTextField(
                    value = form.charPerLine,
                    onValueChange = onUpdateCharPerLine,
                    label = { Text("Karakter per Baris") },
                    supportingText = {
                        Text("Default mengikuti lebar kertas. Ubah manual bila hasil cetak terlalu sempit/lebar.")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Deteksi Status Kertas", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Printer mendukung deteksi kertas habis", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Aktifkan HANYA jika printer mendukung status-query. Kebanyakan printer thermal umum TIDAK mendukung -- deteksi otomatis belum tersedia.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = form.supportsStatusQuery,
                        onCheckedChange = onUpdateSupportsStatusQuery,
                    )
                }

                if (!form.supportsStatusQuery && form.autoDisabledDueToNoResponse) {
                    Text(
                        "Dimatikan otomatis, printer tidak merespons.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(if (isEdit) "Simpan" else "Tambah", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Batal")
            }
        },
    )
}

@Composable
private fun DeletePrinterConfirmDialog(
    printerLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text("Hapus Printer?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        text = {
            Text("Printer \"$printerLabel\" akan dihapus permanen dan tidak bisa dikembalikan. Lanjutkan?", style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ya, Hapus", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Batal")
            }
        },
    )
}

private fun printerSubtitle(printer: PrinterEntity): String =
    when (printer.connectionType) {
        PrinterConnectionType.WIFI -> {
            "${printer.wifiIpAddress}:${printer.wifiPort} • ${printer.charPerLine} kar/baris (${printer.paperWidth.label()})"
        }

        PrinterConnectionType.BLUETOOTH -> {
            "${printer.bluetoothMacAddress ?: "-"} • ${printer.charPerLine} kar/baris"
        }

        PrinterConnectionType.USB -> {
            val vid = printer.usbVendorId
            val pid = printer.usbProductId
            val idLabel = if (vid != null && pid != null) String.format("%04x:%04x", vid, pid) else "-"
            "USB $idLabel • ${printer.charPerLine} kar/baris"
        }
    }

private fun PaperWidth.label(): String =
    when (this) {
        PaperWidth.MM_58 -> "58mm"
        PaperWidth.MM_80 -> "80mm"
    }

private fun PrinterConnectionType.icon() =
    when (this) {
        PrinterConnectionType.WIFI -> Icons.Rounded.Wifi
        PrinterConnectionType.BLUETOOTH -> Icons.Rounded.Bluetooth
        PrinterConnectionType.USB -> Icons.Rounded.Usb
    }