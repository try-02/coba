package com.pos.offline.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.offline.data.backup.BackupManager
import com.pos.offline.data.local.entity.CashierEntity
import com.pos.offline.ui.components.GlassCard
import com.pos.offline.ui.components.LocalGlobalMessage
import com.pos.offline.util.VibrationLevel
import com.pos.offline.util.bouncyOverscroll
import com.pos.offline.util.iosGlideFlingBehavior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    printerViewModel: PrinterViewModel,
    storeProfileViewModel: StoreProfileViewModel,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cashiers by viewModel.cashiers.collectAsStateWithLifecycle()
    val printers by printerViewModel.printers.collectAsStateWithLifecycle()
    val printerUiState by printerViewModel.uiState.collectAsStateWithLifecycle()
    val storeProfile by storeProfileViewModel.profile.collectAsStateWithLifecycle()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
    val soundVolume by viewModel.soundVolume.collectAsStateWithLifecycle()
    val soundDurationMs by viewModel.soundDurationMs.collectAsStateWithLifecycle()
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsStateWithLifecycle()
    val vibrationLevel by viewModel.vibrationLevel.collectAsStateWithLifecycle()
    val vibrationDurationMs by viewModel.vibrationDurationMs.collectAsStateWithLifecycle()
    val globalMessage = LocalGlobalMessage.current

    var showPrinterDialog by remember { mutableStateOf(false) }
    var showStoreProfileDialog by remember { mutableStateOf(false) }

    val exportLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        ) { uri -> if (uri != null) viewModel.exportDatabase(uri) }

    val importLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri -> if (uri != null) viewModel.requestRestore(uri) }

    LaunchedEffect(viewModel, globalMessage) {
        viewModel.messages.collect { msg ->
            globalMessage.showMessage(msg)
        }
    }

    LaunchedEffect(printerViewModel, globalMessage) {
        printerViewModel.messages.collect { msg ->
            globalMessage.showMessage(msg)
        }
    }

    LaunchedEffect(storeProfileViewModel, globalMessage) {
        storeProfileViewModel.messages.collect { msg ->
            globalMessage.showMessage(msg)
        }
    }

    LaunchedEffect(viewModel, globalMessage) {
        viewModel.shareIntent.collect { intent ->
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                globalMessage.showMessage(
                    "Tidak ada aplikasi untuk membagikan file",
                )
            }
        }
    }

    if (uiState.pendingRestoreUri != null) {
        RestoreConfirmDialog(
            onDismiss = { viewModel.cancelRestore() },
            onConfirm = {
                viewModel.confirmRestore {
                    BackupManager.restartApp(context)
                }
            },
        )
    }

    if (uiState.showAddCashierDialog) {
        AddCashierDialog(
            onDismiss = { viewModel.closeAddCashierDialog() },
            onConfirm = { name -> viewModel.addCashier(name) },
        )
    }

    if (showPrinterDialog) {
        PrinterManagementDialog(
            uiState = printerUiState,
            printers = printers,
            onDismiss = { showPrinterDialog = false },
            onOpenAdd = printerViewModel::openAddDialog,
            onOpenEdit = printerViewModel::openEditDialog,
            onCloseForm = printerViewModel::closeFormDialog,
            onSaveForm = printerViewModel::saveForm,
            onUpdateFormLabel = printerViewModel::updateFormLabel,
            onUpdateFormConnectionType = printerViewModel::updateFormConnectionType,
            onUpdateFormPaperWidth = printerViewModel::updateFormPaperWidth,
            onUpdateFormCharPerLine = printerViewModel::updateFormCharPerLine,
            onUpdateFormWifiIp = printerViewModel::updateFormWifiIp,
            onUpdateFormWifiPort = printerViewModel::updateFormWifiPort,
            onUpdateFormSupportsStatusQuery = printerViewModel::updateFormSupportsStatusQuery,
            onRequestDelete = printerViewModel::requestDelete,
            onConfirmDelete = printerViewModel::confirmDelete,
            onCancelDelete = printerViewModel::cancelDelete,
            onSetDefault = printerViewModel::setAsDefault,
            onMovePriorityUp = printerViewModel::movePriorityUp,
            onMovePriorityDown = printerViewModel::movePriorityDown,
            onTestPrint = printerViewModel::testPrint,
        )
    }

    if (showStoreProfileDialog) {
        StoreProfileDialog(
            viewModel = storeProfileViewModel,
            onDismiss = { showStoreProfileDialog = false },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Pengaturan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.statusBars.union(WindowInsets.navigationBars),
    ) { innerPadding ->
        @OptIn(ExperimentalFoundationApi::class)
        CompositionLocalProvider(
            LocalOverscrollFactory provides null,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding()
                    .padding(horizontal = 12.dp)
                    .bouncyOverscroll()
                    .verticalScroll(
                        state = rememberScrollState(),
                        flingBehavior = iosGlideFlingBehavior(),
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(Modifier.height(4.dp))
                SectionLabel("Umpan Balik Pemindai (Scanner Feedback)")
                FuturisticFeedbackControls(
                    isSoundEnabled = isSoundEnabled,
                    soundVolume = soundVolume,
                    soundDurationMs = soundDurationMs,
                    isVibrationEnabled = isVibrationEnabled,
                    vibrationLevel = vibrationLevel,
                    vibrationDurationMs = vibrationDurationMs,
                    onSoundToggle = { viewModel.setSoundEnabled(it) },
                    onSoundVolumeChange = { viewModel.setSoundVolume(it) },
                    onSoundDurationChange = { viewModel.setSoundDurationMs(it) },
                    onTestSound = { viewModel.testSoundPreview() },
                    onVibrationToggle = { viewModel.setVibrationEnabled(it) },
                    onVibrationLevelChange = { viewModel.setVibrationLevel(it) },
                    onVibrationDurationChange = { viewModel.setVibrationDurationMs(it) },
                    onTestVibration = { viewModel.testVibrationPreview() },
                )

                SectionLabel("Cadangkan & Pulihkan")
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Rounded.Info,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Cadangan tersimpan sebagai satu berkas (.db). Simpan ke folder pilihan Anda, atau bagikan langsung ke WhatsApp/Email/Drive — aplikasi tidak menyinkronkan data secara otomatis.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                onClick = { exportLauncher.launch(BackupManager.suggestedBackupFileName()) },
                                enabled = !uiState.isBusy,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (uiState.isExporting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                } else {
                                    Icon(Icons.Rounded.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Simpan", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            OutlinedButton(
                                onClick = { viewModel.shareDatabase() },
                                enabled = !uiState.isBusy,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (uiState.isSharing) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Bagikan", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        OutlinedButton(
                            onClick = { importLauncher.launch("*/*") },
                            enabled = !uiState.isBusy,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isImporting) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Memulihkan…", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Pulihkan Cadangan (Restore)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                SectionLabel("Kelola Kasir")
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (cashiers.isEmpty()) {
                            Text(
                                "Belum ada kasir. Fitur ini opsional — aplikasi tetap bisa dipakai tanpa memilih kasir/shift sama sekali.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            cashiers.forEach { cashier ->
                                CashierRow(
                                    cashier = cashier,
                                    onToggleActive = { active ->
                                        viewModel.setCashierActive(cashier.id, active)
                                    },
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { viewModel.openAddCashierDialog() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Tambah Kasir", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                SectionLabel("Profil Toko & Struk")
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (storeProfile.storeName.isBlank() && storeProfile.logoBytes == null) {
                            Text(
                                "Profil toko belum diatur. Nama toko, alamat, & logo akan tampil di struk cetak.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LogoPreview(
                                    logoBytes = storeProfile.logoBytes,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        storeProfile.storeName.ifBlank { "(Nama toko belum diisi)" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        if (storeProfile.autoPrintEnabled) "Cetak otomatis aktif" else "Cetak otomatis nonaktif",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                        OutlinedButton(
                            onClick = { showStoreProfileDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Edit Profil Toko", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                SectionLabel("Printer Struk")
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (printers.isEmpty()) {
                            Text(
                                "Belum ada printer thermal ditambahkan. Struk tetap bisa dicetak/dibagikan sebagai PDF tanpa printer fisik.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            val defaultPrinter = printers.find { it.isDefault }
                            Text(
                                "Printer utama: ${defaultPrinter?.label ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "${printers.size} printer tersimpan",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        OutlinedButton(
                            onClick = { showPrinterDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Kelola Printer", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                SectionLabel("Sesi Aplikasi")
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Keluar dari aplikasi kasir dengan aman. Semua data penjualan Anda tetap tersimpan utuh di memori lokal perangkat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = onExitClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = "Keluar Aplikasi",
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Keluar Aplikasi", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(96.dp))
            }
        }
    }
}

@Composable
private fun FuturisticFeedbackControls(
    isSoundEnabled: Boolean,
    soundVolume: Int,
    soundDurationMs: Int,
    isVibrationEnabled: Boolean,
    vibrationLevel: VibrationLevel,
    vibrationDurationMs: Int,
    onSoundToggle: (Boolean) -> Unit,
    onSoundVolumeChange: (Int) -> Unit,
    onSoundDurationChange: (Int) -> Unit,
    onTestSound: () -> Unit,
    onVibrationToggle: (Boolean) -> Unit,
    onVibrationLevelChange: (VibrationLevel) -> Unit,
    onVibrationDurationChange: (Int) -> Unit,
    onTestVibration: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.QrCodeScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Respon Scan Real-Time",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            FeedbackSectionCard(
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                title = "Suara Beep",
                subtitle = if (isSoundEnabled) "Aktif ($soundVolume% - $soundDurationMs ms)" else "Nonaktif (OFF)",
                isEnabled = isSoundEnabled,
                onToggle = onSoundToggle,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Volume Suara: $soundVolume%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Slider(
                        value = soundVolume.toFloat(),
                        onValueChange = { onSoundVolumeChange(it.toInt()) },
                        onValueChangeFinished = onTestSound,
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary),
                    )
                    Text(
                        text = "Durasi Beep: $soundDurationMs ms (Aman 50-300 ms)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Slider(
                        value = soundDurationMs.toFloat(),
                        onValueChange = { onSoundDurationChange(it.toInt()) },
                        onValueChangeFinished = onTestSound,
                        valueRange = 50f..300f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary),
                    )
                }
            }
            FeedbackSectionCard(
                icon = Icons.Rounded.Vibration,
                title = "Getaran Haptic",
                subtitle = if (isVibrationEnabled) {
                    val label = when (vibrationLevel) {
                        VibrationLevel.HALUS -> "Halus"
                        VibrationLevel.SEDANG -> "Sedang"
                        VibrationLevel.KUAT -> "Kuat"
                    }
                    "Aktif ($label - $vibrationDurationMs ms)"
                } else {
                    "Nonaktif (OFF)"
                },
                isEnabled = isVibrationEnabled,
                onToggle = onVibrationToggle,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Kekuatan Getar",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val options = listOf(
                            "Halus" to VibrationLevel.HALUS,
                            "Sedang" to VibrationLevel.SEDANG,
                            "Kuat" to VibrationLevel.KUAT,
                        )
                        options.forEach { (label, level) ->
                            val isSelected = vibrationLevel == level
                            if (isSelected) {
                                Button(
                                    onClick = {
                                        onVibrationLevelChange(level)
                                        onTestVibration()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp),
                                ) {
                                    Text(label, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        onVibrationLevelChange(level)
                                        onTestVibration()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp),
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Durasi Getar: $vibrationDurationMs ms",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Slider(
                        value = vibrationDurationMs.toFloat(),
                        onValueChange = { onVibrationDurationChange(it.toInt()) },
                        onValueChangeFinished = onTestVibration,
                        valueRange = 20f..200f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackSectionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val accentColor by animateColorAsState(
        targetValue = if (isEnabled) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "accentColor",
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = activeColor,
                ),
            )
        }
        if (isEnabled) {
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun CashierRow(
    cashier: CashierEntity,
    onToggleActive: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (cashier.active) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = cashier.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
            Text(
                text = if (cashier.active) "Aktif" else "Nonaktif",
                style = MaterialTheme.typography.labelSmall,
                color = if (cashier.active) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                },
            )
        }
        Switch(
            checked = cashier.active,
            onCheckedChange = onToggleActive,
            colors = SwitchDefaults.colors(),
        )
    }
}

@Composable
private fun AddCashierDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text("Tambah Kasir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Kasir") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(name) }, shape = RoundedCornerShape(8.dp)) {
                Text("Tambah", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
    )
}

@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 2.dp),
    )
}

@Composable
private fun RestoreConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text("Pulihkan Cadangan?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "Memulihkan cadangan akan MENGGANTI SELURUH data saat ini (produk, transaksi, kasir) dan menutup aplikasi. Tindakan ini tidak bisa dibatalkan. Lanjutkan?",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ya, Timpa & Pulihkan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Batal")
            }
        },
    )
}