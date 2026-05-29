package com.software.financetracker.feature.backup

import android.net.Uri

sealed interface BackupAction {
    data object OnBackClick : BackupAction
    data object OnExportSave : BackupAction
    data object OnExportShare : BackupAction
    data object OnPickFileClick : BackupAction
    data class OnImportFileSelected(val uri: Uri) : BackupAction
    data object OnImportConfirm : BackupAction
    data object OnImportDismiss : BackupAction
}
