package com.software.financetracker.feature.backup

import com.software.financetracker.core.backup.BackupData

data class BackupState(
    val isLoading: Boolean = false,
    val showImportConfirmDialog: Boolean = false,
    val pendingImportData: BackupData? = null
)
