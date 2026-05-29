package com.software.financetracker.feature.backup

sealed interface BackupEvent {
    data object NavigateBack : BackupEvent
    data object LaunchFilePicker : BackupEvent
    data class ShowSnackbar(val message: String) : BackupEvent
}
