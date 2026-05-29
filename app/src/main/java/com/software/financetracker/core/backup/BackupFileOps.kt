package com.software.financetracker.core.backup

import android.net.Uri

interface BackupFileOps {
    suspend fun saveToDownloads(json: String, filename: String): Boolean
    suspend fun shareJson(json: String, filename: String)
    suspend fun readFromUri(uri: Uri): String
}
