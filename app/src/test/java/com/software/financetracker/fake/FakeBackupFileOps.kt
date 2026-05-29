package com.software.financetracker.fake

import android.net.Uri
import com.software.financetracker.core.backup.BackupFileOps

class FakeBackupFileOps : BackupFileOps {

    var saveResult = true
    var shouldThrowOnRead = false
    var readResult: String = ""
    val sharedFiles = mutableListOf<String>()
    val savedFiles = mutableListOf<String>()

    override suspend fun saveToDownloads(json: String, filename: String): Boolean {
        if (saveResult) savedFiles.add(filename)
        return saveResult
    }

    override suspend fun shareJson(json: String, filename: String) {
        sharedFiles.add(filename)
    }

    override suspend fun readFromUri(uri: Uri): String {
        if (shouldThrowOnRead) throw RuntimeException("Read failed")
        return readResult
    }
}
