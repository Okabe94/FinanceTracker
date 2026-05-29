package com.software.financetracker.core.backup

interface BackupRepository {
    suspend fun export(): BackupData
    suspend fun import(data: BackupData)
}
