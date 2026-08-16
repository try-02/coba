package com.toko.pos.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object BackupManager {
    private const val BACKUP_DIR = "pos_backups"

    fun backupDatabase(context: Context): Uri? {
        return try {
            val dbFile = context.getDatabasePath("pos.db")
            val backupDir = File(context.getExternalFilesDir(null), BACKUP_DIR)
            backupDir.mkdirs()
            val timestamp = System.currentTimeMillis()
            val backupFile = File(backupDir, "pos_backup_$timestamp.db")
            dbFile.copyTo(backupFile, overwrite = true)
            Uri.fromFile(backupFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun restoreDatabase(context: Context, uri: Uri): Boolean {
        return try {
            val dbFile = context.getDatabasePath("pos.db")
            val input = context.contentResolver.openInputStream(uri) ?: return false
            input.use { inputStream ->
                FileOutputStream(dbFile).use { output ->
                    inputStream.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}