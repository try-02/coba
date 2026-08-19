package com.pos.offline.ui.settings

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pos.offline.util.BluetoothDeviceInfo
import com.pos.offline.util.PermissionUtils
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun BluetoothPickerDialog(
    viewModel: PrinterViewModel,
    onDismiss: () -> Unit,
) {
    val state by viewModel.bluetoothUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentOnDismiss by rememberUpdatedState(onDismiss) 
    val lifecycleOwner = LocalLifecycleOwner.current
    var permissionState by remember {
        mutableStateOf(PermissionUtils.currentBluetoothPermissionState(context))
    }
    var btEnabled by remember { mutableStateOf(viewModel.isBluetoothEnabled()) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) {
            PermissionUtils.markBluetoothPermissionRequested(context)
            permissionState = PermissionUtils.currentBluetoothPermissionState(context)
            if (permissionState == PermissionUtils.BluetoothPermissionState.Granted) {
                viewModel.refreshBondedDevices()
            }
        }

    val enableBtLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            btEnabled = viewModel.isBluetoothEnabled()
            if (permissionState == PermissionUtils.BluetoothPermissionState.Granted && btEnabled) {
                viewModel.refreshBondedDevices()
            }
        }

    LaunchedEffect(Unit) {
        if (permissionState == PermissionUtils.BluetoothPermissionState.Granted) {
            viewModel.refreshBondedDevices()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.pairingSuccess.collect { currentOnDismiss() }
    }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    permissionState = PermissionUtils.currentBluetoothPermissionState(context)
                    btEnabled = viewModel.isBluetoothEnabled()
                    if (permissionState == PermissionUtils.BluetoothPermissionState.Granted && btEnabled) {
                        viewModel.refreshBondedDevices()
                    }
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetBluetoothPicker() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Bluetooth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Pilih Printer Bluetooth", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (permissionState) {
                    PermissionUtils.BluetoothPermissionState.PermanentlyDenied -> {
                        Text(
                            "Izin Bluetooth ditolak dan tidak bisa diminta lagi lewat dialog ini. " +
                                "Aktifkan secara manual lewat Pengaturan Aplikasi > Izin > Perangkat di Sekitar/Bluetooth.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null),
                                )
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Buka Pengaturan Aplikasi", fontWeight = FontWeight.Bold)
                        }
                    }

                    PermissionUtils.BluetoothPermissionState.CanRequest -> {
                        Text(
                            "Aplikasi memerlukan izin Bluetooth untuk mencari & memasangkan printer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = { permissionLauncher.launch(PermissionUtils.requiredBluetoothPermissions()) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Izinkan Akses Bluetooth", fontWeight = FontWeight.Bold)
                        }
                    }

                    PermissionUtils.BluetoothPermissionState.Granted -> {
                        if (!btEnabled) {
                            Text(
                                "Bluetooth perangkat sedang nonaktif. Aktifkan terlebih dahulu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Button(
                                onClick = { enableBtLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Aktifkan Bluetooth", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Perangkat Terpasang", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            if (state.bondedDevices.isEmpty()) {
                                Text(
                                    "Belum ada printer ter-pairing di pengaturan Bluetooth HP ini.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                state.bondedDevices.forEach { device ->
                                    DeviceRow(device = device, trailing = null) {
                                        viewModel.selectBondedDevice(device)
                                        onDismiss()
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Perangkat Baru", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                TextButton(
                                    onClick = {
                                        if (state.isScanning) viewModel.stopDiscovery() else viewModel.startDiscovery()
                                    },
                                ) {
                                    Text(
                                        if (state.isScanning) "Berhenti" else "Cari Perangkat",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (state.isScanning) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Mencari perangkat di sekitar…", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            val newDevices = state.discoveredDevices.filterNot { found ->
                                state.bondedDevices.any { it.address == found.address }
                            }
                            newDevices.forEach { device ->
                                DeviceRow(device = device, trailing = "Pasang") {
                                    viewModel.requestPairing(device)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        },
    )

    state.pairingTarget?.let { target ->
        BluetoothPinDialog(
            device = target,
            isPairing = state.isPairing,
            onDismiss = { viewModel.cancelPairing() },
            onConfirm = { pin -> viewModel.confirmPairing(pin) },
        )
    }
}

@Composable
private fun DeviceRow(
    device: BluetoothDeviceInfo,
    trailing: String?,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp) // Accessible 48dp target
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(device.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    device.address,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace, // Technical Monospace
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (trailing != null) {
                Text(
                    trailing,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BluetoothPinDialog(
    device: BluetoothDeviceInfo,
    isPairing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var pin by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { if (!isPairing) onDismiss() },
        title = { Text("Masukkan PIN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Pasangkan dengan \"${device.name}\". PIN printer thermal umumnya 0000 atau 1234, namun bisa berbeda tiap unit.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { pin = "0000" },
                        enabled = !isPairing,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("0000", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { pin = "1234" },
                        enabled = !isPairing,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("1234", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it.filter { c -> c.isDigit() } },
                    label = { Text("PIN Kustom") },
                    singleLine = true,
                    enabled = !isPairing,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(pin) },
                enabled = !isPairing && pin.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isPairing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Pasangkan", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isPairing) { Text("Batal") }
        },
    )
}
