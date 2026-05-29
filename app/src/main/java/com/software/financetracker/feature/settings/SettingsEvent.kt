package com.software.financetracker.feature.settings

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
    data object NavigateToBackup : SettingsEvent
    data class ShowSnackbar(val message: String) : SettingsEvent
}
