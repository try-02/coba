package com.toko.pos.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.toko.pos.utils.BackupManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        uri?.let { target ->
            val source = BackupManager.backupDatabase(context)
            if (source != null) {
                context.contentResolver.openOutputStream(target)?.use { output ->
                    context.contentResolver.openInputStream(source)?.use { input -> input.copyTo(output) }
                }
                Toast.makeText(context, "Backup berhasil", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Backup gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            if (BackupManager.restoreDatabase(context, it)) {
                Toast.makeText(context, "Restore berhasil. Aplikasi akan dimuat ulang.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Restore gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { backupLauncher.launch("pos_backup_${System.currentTimeMillis()}.db") },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Backup Data")
            }

            Button(
                onClick = { restoreLauncher.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Restore, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Restore Data")
            }

            Divider()

            Text("Tentang Aplikasi", style = MaterialTheme.typography.titleMedium)
            Text(
                "POS Offline v1.0\nAplikasi kasir 100% offline. Data disimpan lokal di perangkat.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}