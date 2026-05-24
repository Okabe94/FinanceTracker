package com.software.financetracker.feature.settings

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
    data class ShowSnackbar(val message: String) : SettingsEvent
}
